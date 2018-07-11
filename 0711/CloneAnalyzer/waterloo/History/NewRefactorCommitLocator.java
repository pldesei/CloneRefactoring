package waterloo.History;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.RefactoredInstance;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import waterloo.Util.GlobalSettings;

public class NewRefactorCommitLocator {
	public Vector<String> commits = new Vector<String>();
	public Vector<String> filteredCommits = new Vector<String>();
	String lineBreak="\n";
	String projectPath=null,filteredLogFilePath=null,totalLogFilePath=null;
	
	String repoPath = null;
	
	public NewRefactorCommitLocator(String projectPath,String filteredLogFilePath,String totalLogFilePath, String repoPath){
		this.projectPath=projectPath;
		this.filteredLogFilePath=filteredLogFilePath;
		this.totalLogFilePath=totalLogFilePath;	
		
		this.repoPath = repoPath;
	}
	
	public static ByteArrayOutputStream read(String gitRepoPath, String revision, String filePath) {  
        ByteArrayOutputStream out = new ByteArrayOutputStream(); 
        Git git = null;
        Repository repository = null;  
        RevWalk walk = null;
        TreeWalk treeWalk = null;
        try {  
            
            git = Git.open(new File(gitRepoPath));  
            repository = git.getRepository();  
            walk = new RevWalk(repository);  
            ObjectId objId = repository.resolve(revision);  
            RevCommit revCommit = walk.parseCommit(objId);  
            RevTree revTree = revCommit.getTree();  
 
            treeWalk = TreeWalk.forPath(repository, filePath, revTree);  
            if (treeWalk == null)
            	return null;
            ObjectId blobId = treeWalk.getObjectId(0);  
            ObjectLoader loader = repository.open(blobId); 
            loader.copyTo(out);
            
            git.close();
            repository.close();
            walk.close();
            treeWalk.close();
            
        } catch (Exception e) {  
             e.printStackTrace();
        } finally {  
            if (repository != null)  
                repository.close(); 
            
            git.close();
            repository.close();
            walk.close();
            if (treeWalk != null)
            	treeWalk.close();
        }  
  
        return out;  
    }
	
	public void setHashvalueList(){
		try {
			BufferedReader input = new BufferedReader(new FileReader(new File(totalLogFilePath)));
			String tmp = "";
			while ((tmp = input.readLine()) != null) {
				this.commits.addElement(tmp.substring(0, tmp.indexOf(",")));
			}
			input.close();
			input = new BufferedReader(new FileReader(new File(filteredLogFilePath)));
			while ((tmp = input.readLine()) != null) {
				this.filteredCommits.addElement(tmp.substring(0, tmp.indexOf(",")));
			}
			input.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getRealChangeID (RefactoredInstance ins, Double simi) { 
		int start = Integer.parseInt(ins.getFragments().get(0).getVersionRepoName());
		int end = Integer.parseInt(ins.getCommonMethod().getVersionRepoName());
		String startVersion = filteredCommits.elementAt(filteredCommits.size() - start);
		String endVersion = filteredCommits.elementAt(filteredCommits.size() - end);
		
		Vector<String> commit = new Vector<String>();
		boolean flag = false;
		for (int i = 0; i < commits.size(); i++) {
			if (commits.elementAt(i).equals(endVersion)) {
				flag = true;
			}
			if (flag) {
				commit.addElement(commits.elementAt(i));
			}
			if (commits.elementAt(i).equals(startVersion)) {
				break;
			}
		}
		
		if (commit.size() == 2) {
			return endVersion;
		}
		else {
			System.out.println("CommitSize:"+commit.size());
		}
		
		Vector<String> commonMethodContents = new Vector<String>();
		try {
			MyFragment commonMethod=ins.getCommonMethod();
			BufferedReader input = new BufferedReader(new FileReader(new File(commonMethod.getFilePath())));
			String tmp = "";
			int cnt = 0;
			while ((tmp = input.readLine()) != null) {
				cnt ++;
				if (cnt >= commonMethod.getStartLine() && cnt <= commonMethod.getEndLine()) {
					commonMethodContents.addElement(tmp);
				}
				if (cnt == commonMethod.getEndLine())
					break;
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Vector<Double> trueRet = new Vector<Double>();
		trueRet = calcSimilarity(ins, commit.elementAt(0));
		Collections.sort(trueRet);
		//System.out.println("true ret:" + trueRet);
		int i, pc = 0;
		for (i = commit.size() - 1 ; i >= 0; i--) {
			if (i == 0)
				pc = 1;
			if (judge(commit.elementAt(i), ins, commonMethodContents)) {
				Vector<Double> ret = calcSimilarity(ins, commit.elementAt(i));
				//System.out.println("orginal:" + ins.simis);
				
				Collections.sort(ret);
				//System.out.println(ret);
				if (ret.elementAt(0) >= simi) {
					break;
				}
				/*boolean flag1 = true;
				if (ret.size() == trueRet.size()) {
					
					for (int j = 0; j < ret.size(); j++) {
						if (Math.abs(ret.elementAt(j) - trueRet.elementAt(j)) > 1e-10)
							flag1 = false;
					}
				}
				if (flag1)
					break;*/
				
			}
		}
		
		if (i < 0) {
			i = 0;
			//System.out.println("why come here");
			//System.out.println(startVersion);
			//System.out.println(endVersion);
			return commit.elementAt(i);
		}
		//System.out.println("finally");
		return commit.elementAt(i);
	}
	
	public Vector<Double> calcSimilarity(RefactoredInstance ins, String ID) {
		Vector<MyFragment> frags = ins.getFragments();
		MyFragment common = ins.getCommonMethod();
		Vector<Double> ret = new Vector<Double>();
		GZTokenVisitor commonv = null;
		try {
			FileInputStream in = new FileInputStream(common.getFilePath());
			CompilationUnit cu = JavaParser.parse(in);
			commonv = new GZTokenVisitor();
			commonv.visit(cu, null);
			in.close();
		} catch (Exception ex) {
			System.out.println("common file miss:" + common.getFilePath());
			return ret;
		}
		catch (Error err) {
			System.out.println("Error happened: \n" + common.getFilePath());
			return ret;
		}
		Vector<String> methodContent = new Vector<String>();
		for (int i = common.getStartLine(); i <= common.getEndLine(); i++) {
			Vector<String> t = commonv.tokens.get(i);
			if (t == null || t.size() == 0)
				continue;
			else {
				for (int j = 0; j < t.size(); j++)
					methodContent.addElement(t.elementAt(j));
			}
		}
		//System.out.println(methodContent);
		int methodLen = methodContent.size();
		
		int fragLen = 0;
		
		/*
		String cmd = "cd " + projectPath + lineBreak+"git reset ";
		String cmdnow = cmd + ID + " --hard"+lineBreak;
		if (exec(cmdnow) != 0)
			System.out.println("shell error here");
		*/
		for (int ii = 0; ii < frags.size(); ii++) {
			MyFragment frag = frags.elementAt(ii);
			String predPath = frag.getFilePath();
					
			ByteArrayOutputStream out = read(projectPath, ID, getRepoRelativeFilePath(predPath));
			ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
			BufferedReader reader = null;
			List<String> currentValue = new Vector<String>();
			List<String> predValue = new Vector<String>();
			try {
				reader = new BufferedReader(new InputStreamReader(input));
				String tmp = null;
				while ((tmp = reader.readLine()) != null) {
					currentValue.add(tmp);
				}
				currentValue.add("");
				input.close();
				reader.close();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					input.close();
					reader.close();
				} catch(Exception ee) {
					ee.printStackTrace();
				}
			}
			
			try{
	            reader = new BufferedReader(new FileReader(predPath));
	            String tmp = null;
				while ((tmp = reader.readLine()) != null) {
	            	predValue.add(tmp);
	            }
	            predValue.add("");
	            reader.close();
	        }catch(IOException e){
	            e.printStackTrace();
	        } finally {
	        	try {
	        		reader.close();
	        	} catch(Exception ee) {
	        		ee.printStackTrace();
	        	}
	        }
			
			/*
			int index = fragPath.indexOf(GlobalSettings.pathSep);
			fragPath = fragPath.substring(index + 1);
			index = fragPath.indexOf(GlobalSettings.pathSep);
			fragPath = fragPath.substring(index + 1);
			index = fragPath.indexOf(GlobalSettings.pathSep);
			fragPath = fragPath.substring(index + 1);
			String curPath = projectPath + fragPath;
			//System.out.println(curPath);
			 */
			int[] linemap = buildLineMap(currentValue, predValue);
			HashSet<Integer> lineSet = new HashSet<Integer>();
			if (linemap != null) {
				for (int i = frag.getStartLine(); i <= frag.getEndLine(); i++) {
					if (linemap[i-1] == -1)
						lineSet.add(i);
				}
			}
			else {
				System.out.println("linemap is null");
				for (int i = frag.getStartLine(); i <= frag.getEndLine(); i++) {
					lineSet.add(i);
				}
			}
			GZTokenVisitor fragv = null;
			try {
				FileInputStream in = new FileInputStream(predPath);
				CompilationUnit cu = JavaParser.parse(in);
				fragv = new GZTokenVisitor();
				fragv.visit(cu, null);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			catch (Error err) {
				System.out.println("Error happened: \n" + frag.getFilePath());
				continue;
			}
			
			Vector<String> fragContent = new Vector<String>();
			for (int i = frag.getStartLine(); i <= frag.getEndLine(); i++) {
				if (!lineSet.contains(i))
					continue;
				Vector<String> t = fragv.tokens.get(i);
				if (t == null || t.size() == 0)
					continue;
				else {
					for (int j = 0; j < t.size(); j++)
						fragContent.addElement(t.elementAt(j));
				}
			}
			//System.out.println(fragContent);
			fragLen = fragContent.size();
			int insertDistance = 0, deleteDistance = 0, modifyDistance = 0;
			int[][] dis = new int[fragContent.size() + 10][methodContent.size() + 10];
			int[][] preDis = new int[fragContent.size() + 10][methodContent.size() + 10];
			for (int i = 0; i < fragContent.size(); i++)
				for (int j = 0; j < methodContent.size(); j++) {
					if (i == 0)
						dis[i][j] = j;
					else {
						if (j == 0)
							dis[i][j] = i;
						else
							dis[i][j] = 2147483647;
					}
				}

			for (int i = 0; i < fragContent.size(); i++)
				for (int j = 0; j < methodContent.size(); j++)
					preDis[i][j] = -1;
			for (int i = 1; i <= fragContent.size(); i++) {
				for (int j = 1; j <= methodContent.size(); j++) {
					if (fragContent.elementAt(i - 1).equals(methodContent.elementAt(j - 1))) {
						dis[i][j] = dis[i - 1][j - 1];
						preDis[i][j] = 0;
					} else {
						int insertion = dis[i - 1][j] + 1;
						int deletion = dis[i][j - 1] + 1;
						int substitution = dis[i - 1][j - 1] + 1;
						if (substitution <= insertion && substitution <= deletion) {
							dis[i][j] = substitution;
							preDis[i][j] = 1;
						} else {
							if (insertion <= substitution && insertion <= deletion) {
								dis[i][j] = insertion;
								preDis[i][j] = 2;
							} else {
								dis[i][j] = deletion;
								preDis[i][j] = 3;
							}
						}
					}
				}
			}
			int i = fragContent.size();
			int j = methodContent.size();
			while (i > 0 && j > 0) {
				if (preDis[i][j] == 1) {
					modifyDistance++;
					i = i - 1;
					j = j - 1;
				}
				if (preDis[i][j] == 2) {
					insertDistance++;
					i = i - 1;
				}
				if (preDis[i][j] == 3) {
					deleteDistance++;
					j = j - 1;
				}
				if (preDis[i][j] == 0) {
					i = i - 1;
					j = j - 1;
				}
			}
			if (i > 0)
				insertDistance += i;
			if (j > 0)
				deleteDistance += j;
			int distance = insertDistance + modifyDistance + deleteDistance;
			ret.addElement(1 - (double)distance/Math.max(methodLen, fragLen));
		}
		
		return ret;
	}
	
	public String getRepoRelativeFilePath(String cloneFilePath) {
		
		String filePathWithoutRepoFolder = cloneFilePath.substring(this.repoPath.length());
		String[] tmp = filePathWithoutRepoFolder.split(GlobalSettings.pathSep);
		int relativePathStart = tmp[0].length() + tmp[1].length() + 2;
		return filePathWithoutRepoFolder.substring(relativePathStart);
	}
	
	public static int[] buildLineMap(List<String> currentValue, List<String> predValue) {
		int[] linemap = null;
        if(linemap == null){
            
            Patch p = DiffUtils.diff(predValue, currentValue);
            List<Delta> deltas = p.getDeltas();
                
            for(int i = 0; i<deltas.size(); i++){
                for(int j = i+1; j<deltas.size(); j++){
                    if(deltas.get(i).getOriginal().getPosition()<deltas.get(j).getOriginal().getPosition()){
                        Delta d = deltas.get(i);
                        deltas.set(i, deltas.get(j));
                        deltas.set(j, d);
                    }
                }
            }
        
            linemap = new int[predValue.size() + 1];
            int index1 = 0;
            int index2 = 0;
        
            for(int k = deltas.size()-1;k>=0;k--){
                Delta del = deltas.get(k);
                int lineNumber = del.getOriginal().getPosition();
                int linesOld = del.getOriginal().getLines().size();
                int linesNew = del.getRevised().getLines().size();
                for(int i = index1; i<lineNumber;i++){
                    linemap[i] = index2; 
                    index2++;
                }
                index1=lineNumber;
                if(del.getType().equals(Delta.TYPE.INSERT)) {
                    index2+=linesNew;
                }else if(del.getType().equals(Delta.TYPE.DELETE)){
                    for(int i = index1; i<index1+linesOld; i++){
                        linemap[i] = -1;
                    }
                    index1+=linesOld;
                }else if(del.getType().equals(Delta.TYPE.CHANGE)){
                    for(int i = index1; i<index1+linesOld; i++){
                        linemap[i] = -1;
                    }
                    index1+=linesOld;
                    index2+=linesNew;
                }
            }
            for(int i = index1; i<predValue.size();i++){
                linemap[i] = index2;
                index2++;
            }
        }
        
        return linemap;
	}
	
	public boolean judge(String now, RefactoredInstance ins, Vector<String> commonMethodContents) {
	
		String fragPath = ins.getCommonMethod().getFilePath();
		
		ByteArrayOutputStream out = read(projectPath, now, getRepoRelativeFilePath(fragPath));
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		//String[] contents = str.split("\n");
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String tmp = null;
			boolean flag = true;
			boolean flag1 = false;
			while ((tmp = reader.readLine()) != null) {
				if (tmp.equals(commonMethodContents.elementAt(0))) {
					flag1 = true;
					flag = true;
					//System.out.println("flag1");
					for (int i = 1; i < commonMethodContents.size(); i++) {
						tmp = reader.readLine();
						if (!tmp.equals(commonMethodContents.elementAt(i))) {
							//System.out.println("flag");
							flag = false;
							break;
						}
					}
				}
				//if (flag1)
					//break;
			}
			//System.out.println(cnt);
			in.close();
			reader.close();
			if (flag1 && flag) {
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
		
		/*
		String cmd =  "cd " + projectPath + lineBreak+"git reset ";
		String cmdnow = cmd + now + " --hard"+lineBreak;
		if (exec(cmdnow) != 0) 
			System.out.println("shell run error");
		
		int index = fragPath.indexOf(GlobalSettings.pathSep);
		fragPath = fragPath.substring(index + 1);
		index = fragPath.indexOf(GlobalSettings.pathSep);
		fragPath = fragPath.substring(index + 1);
		index = fragPath.indexOf(GlobalSettings.pathSep);
		fragPath = fragPath.substring(index + 1);
		String path = projectPath + fragPath;
		//System.out.println(path);
		boolean flag = true;
		boolean flag1 = false;
		BufferedReader input = null;
		if (!new File(path).exists()) {
			System.out.println("not exist");
			return false;
		}
			
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String tmp = "";
			while ((tmp = input.readLine()) != null) {
				if (tmp.equals(commonMethodContents.elementAt(0))) {
					flag1 = true;
					//System.out.println("flag1");
					for (int i = 1; i < commonMethodContents.size(); i++) {
						tmp = input.readLine();
						if (!tmp.equals(commonMethodContents.elementAt(i))) {
							//System.out.println("flag");
							flag = false;
							break;
						}
					}
				}
				if (flag1)
					break;
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (flag1 && flag) {
			return true;
		}
		else {
			return false;
		}
		*/
	}
	
	/*public static int exec(String cmd) {
		System.out.println("shell command: " + cmd);
		try {
			FileOutputStream fos = new FileOutputStream("reset.sh");
			String w = cmd;
			fos.write(w.getBytes());
			fos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p = runtime.exec("bash reset.sh");
			if (p.waitFor() != 0) 
				System.out.println("error");
			final InputStream is1 = p.getErrorStream();
		    Thread thread = new Thread(new Runnable() {
		   		public void run() {
		        	BufferedReader br = new BufferedReader(new InputStreamReader(is1));
		        	try{
		        		System.out.println("hello");
		        		String tmp = "";
		                while((tmp = br.readLine()) != null) 
		                	System.out.println(tmp);
		                System.out.println("hello again");
		            } catch(Exception e) {
		            	e.printStackTrace();
		            }
		        }
		    });
		    thread.start();
		    thread.join();
		    InputStream is2 = p.getInputStream();
		    BufferedReader br2 = new BufferedReader(new InputStreamReader(is2)); 
		    StringBuilder buf = new StringBuilder(); 
		    String line = null;
		    while((line = br2.readLine()) != null) buf.append(line); // 
		    System.out.println("command: " + cmd + " done!");
		    System.out.println("Buf:"+buf);
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	*/
}
