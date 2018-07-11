package edu.pku.sei.codeclone.predictor.datacollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import edu.pku.sei.codeclone.predictor.ChangeGenerator;
import edu.pku.sei.codeclone.predictor.GZTokenVisitor;
import edu.pku.sei.codeclone.predictor.MyCloneClass;
import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.MyVersion;
import edu.pku.sei.codeclone.predictor.MyVersionList;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class RefactoredCollection {

	public static String refactoredOutputPath = "D:\\gztest2\\refactored4.txt";
	public static String refactoredOutputReadablePath = "D:\\gztest2\\refactored_readable4.txt";
	
	public static HashMap<String, HashMap<String, HashMap<String, MethodDeclaration>>> versions = new HashMap<String, HashMap<String, HashMap<String, MethodDeclaration>>>();
	public static HashMap<RefactoredInstance, Vector<Double>> groupsWithSimilarity = new HashMap<RefactoredInstance, Vector<Double>> ();
	public static HashMap<RefactoredInstance, Double> maxSimi = new HashMap<RefactoredInstance, Double>();
	
	public static void main(String args[]) throws Exception {
		filterRefactoredList(buildGenealogy("D:\\antFilter\\", "D:\\antFilter\\"));
		/*BufferedWriter w = new BufferedWriter(new FileWriter("D:\\gztest2\\2201-2823.txt"));
		for (MyRefactoredClass refactoredClass: refactoredList) {
			MyCloneClass pred = refactoredClass.pred;
			MyCloneClass next = refactoredClass.next;
			Vector<MyFragment> frags = new Vector<MyFragment>();
			if (next == null) {
				for (MyFragment frag : pred.fragments) {
					frags.addElement(frag);
				}
			}
			else {
				HashSet<MyFragment> myset = new HashSet<MyFragment>();
				for (MyFragment frag : next.fragments) {
					myset.add(frag.predecessor);
				}
				for (MyFragment frag : pred.fragments) 
					if (!myset.contains(frag)) {
						frags.addElement(frag);
					}
			}
			if (next == null)
				w.write("Group # no instance in this group has a subsequent clone\r\n");
			else
				w.write("Group #\r\n");
			w.write(frags.toString()+"\r\n");
		}
		w.close();
		*/
	}
	
	private static class MethodVisitor extends VoidVisitorAdapter<Void> {
    	
    	public HashMap<Integer, Vector<String>> methodInvoke = new HashMap<Integer, Vector<String>>();
    	public HashMap<String, MethodDeclaration> methodDeclare = new HashMap<String, MethodDeclaration>();
    	
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            /* here you can access the attributes of the method.
             this method will be called for all methods in this 
             CompilationUnit, including inner class methods */
            //System.out.println(n.getName() + " " + n.getBeginLine() + ", " + n.getEndLine());
        	String name = n.getName();
        	if (name.indexOf(".") != -1) {
        		name = name.substring(name.lastIndexOf(".") + 1);
        	}
            methodDeclare.put(name, n);
            super.visit(n, arg);
        }
        
        @Override
        public void visit(MethodCallExpr n, Void arg) {
        	//System.out.println("Line: " + n.getBeginLine() + " " + n.getName() + " " + n.getArgs());
        	String name = n.getName();
        	if (name.indexOf(".") != -1) {
        		name = name.substring(name.lastIndexOf(".") + 1);
        	}
        	if (methodInvoke.containsKey(n.getBeginLine())) {
        		Vector<String> tmp = methodInvoke.get(n.getBeginLine());
        		tmp.add(name);
        	}
        	else {
        		Vector<String> tmp = new Vector<String>();
        		tmp.add(name);
        		methodInvoke.put(n.getBeginLine(), tmp);
        	}
        	super.visit(n, arg);
        }
    }

	public static Vector<MyRefactoredClass> buildGenealogy(String srcPath, String resPath) {
	
	    List<MyVersion> versions = MyVersionList.load(srcPath, resPath);
	
	    Vector<MyRefactoredClass> refactoredList = new Vector<MyRefactoredClass>();
	    for (int i = 1; i < versions.size(); i++) {
	    	MyVersion now = versions.get(i);
	    	MyVersion last = versions.get(i - 1);
	    	Vector<MyCloneClass> predCloneClassList = new Vector<MyCloneClass>();
	    	for (int j = 0; j < now.clones.size(); j++) {
	    		MyCloneClass nowClass = now.clones.get(j);
	    		HashMap<MyCloneClass, Integer> counter = new HashMap<MyCloneClass, Integer>();
	    		for (int k = 0; k < nowClass.fragments.size(); k++) {
	    			MyFragment nowFrag = nowClass.fragments.get(k);
	    			if (nowFrag.predecessor != null) {
	    				MyCloneClass fatherCloneClass = nowFrag.predecessor.fatherCloneClass;
	    				predCloneClassList.addElement(fatherCloneClass);
	    				if (fatherCloneClass != null) {
	    					if (counter.containsKey(fatherCloneClass)) counter.put(fatherCloneClass, counter.get(fatherCloneClass) + 1);
	    					else counter.put(fatherCloneClass, 1);
	    				}
	    			}
	    		}
	    		int max = -1;
	    		MyCloneClass predCloneClass = null;
	    		for (MyCloneClass fatherClass: counter.keySet()) {
	    			int times = counter.get(fatherClass);
	    			if (times > max) { 
	    				max = times; 
	    				predCloneClass = fatherClass;
	    			}
	    		}
	    		if (predCloneClass != null) {
	    			HashSet<MyFragment> myset = new HashSet<MyFragment>();
					for (MyFragment frag : nowClass.fragments) {
						myset.add(frag.predecessor);
					}
					int cnt = 0;
					for (MyFragment frag : predCloneClass.fragments) 
						if (!myset.contains(frag)) {
							cnt ++;
						}
					if (cnt >= 2)
	    				refactoredList.addElement(new MyRefactoredClass(predCloneClass, nowClass, now.versionRepoID));
	      		}
	    	}
	    	for (MyCloneClass lastCloneClass: last.clones) {
	    		if (!predCloneClassList.contains(lastCloneClass)) {
	    			refactoredList.addElement(new MyRefactoredClass(lastCloneClass, null, now.versionRepoID));
	    		}
			}
	    }
		return refactoredList;
		
	}
	
	private static void filterRefactoredList(Vector<MyRefactoredClass> refactoredList) throws Exception {
		int resultCnt = 0;
		Vector<RefactoredInstance> refactoredInstanceList = new Vector<RefactoredInstance>();
		int method1 = 0;
		int method2 = 0;
		int method3 = 0;
		for (MyRefactoredClass refactoredClass: refactoredList) {
			MyCloneClass pred = refactoredClass.pred;
			MyCloneClass next = refactoredClass.next;
			Vector<MyFragment> frags = new Vector<MyFragment>();
			HashMap<String, Vector<MyFragment>> methodInvokeByFragments = new HashMap<String, Vector<MyFragment>>();
			if (next == null) {
				for (MyFragment frag : pred.fragments) {
					if (!frags.contains(frag))
						frags.addElement(frag);
				}
			}
			else {
				HashSet<MyFragment> myset = new HashSet<MyFragment>();
				for (MyFragment frag : next.fragments) {
					myset.add(frag.predecessor);
				}
				for (MyFragment frag : pred.fragments) 
					if (!myset.contains(frag)) {
						if (!frags.contains(frag))
							frags.addElement(frag);
					}
			}
			
			for (MyFragment frag : frags) {
				Vector<String> methodInvokeList = detectAddedMethodInvoke(frag, refactoredClass.nextVersionRepoID);
				if (methodInvokeList == null) continue;
				for (String name : methodInvokeList) {
					if (methodInvokeByFragments.get(name) != null) {
						Vector<MyFragment> list = methodInvokeByFragments.get(name);
						if (!list.contains(frag))
							list.add(frag);
						methodInvokeByFragments.put(name, list);
					}
					else {
						Vector<MyFragment> list = new Vector<MyFragment>();
						list.add(frag);
						methodInvokeByFragments.put(name, list);
					}
				}
			}
			
			if (methodInvokeByFragments.size() > 0)
 				method1 ++;
			
			HashMap<String, Vector<MyFragment>> tmp = new HashMap<String, Vector<MyFragment>>();
			for (String name : methodInvokeByFragments.keySet()) {
				if (methodInvokeByFragments.get(name).size() >= 2) 
					tmp.put(name, methodInvokeByFragments.get(name));
			}
			methodInvokeByFragments = tmp;

			if (methodInvokeByFragments.size() > 0)
				method2 ++;
			else 
				continue;
			
			HashMap<String, Vector<MyFragment>> ttmp = new HashMap<String, Vector<MyFragment>>();
			for (String name : methodInvokeByFragments.keySet()) {
				Vector<MyFragment> frag = methodInvokeByFragments.get(name);
				boolean flag = false;
				for (RefactoredInstance i : refactoredInstanceList) {
					if (i.frags.size() == frag.size()) {
						HashSet<Integer> set = new HashSet<Integer>();
						for (MyFragment f : frag) {
							int min = 2147483647;
							int which = 0;
							for (int j = 0; j < i.frags.size(); j++) {
								MyFragment ff = i.frags.elementAt(j);
								if (f.srcPath.equals(ff.srcPath) && !set.contains(j)) {
									if (ff.endLine >= f.endLine && ff.startLine <= f.startLine) {
										int t = ff.endLine - ff.startLine;
										if (t < min) {
											min = t;
											which = j;
										}
									}
								}
							}
							if (min < 2147483647)
								set.add(which);
						}
						if (set.size() == frag.size()) {
							flag = true;
							break;
						}
					}
				}
				if (!flag) {
					ttmp.put(name, methodInvokeByFragments.get(name));
				}
			}
			methodInvokeByFragments = ttmp;
			
			if (methodInvokeByFragments.size() == 0) {
				continue;
			}
			
			if (next == null) {
				if (!versions.containsKey(refactoredClass.nextVersionRepoID)) {
					String nextPath = pred.versionBelongs.basePath.replace("\\" + pred.versionBelongs.versionRepoID, "\\" + refactoredClass.nextVersionRepoID);
					getMethodDeclaration(nextPath);
				}
			}
			else {
				if (!versions.containsKey(next.versionBelongs.versionRepoID)) {
					getMethodDeclaration(next.versionBelongs.basePath);
				}
			}
			if (!versions.containsKey(pred.versionBelongs.versionRepoID)) {
				getMethodDeclaration(pred.versionBelongs.basePath);
			}
			HashMap<String, HashMap<String, MethodDeclaration>> add = new HashMap<String, HashMap<String, MethodDeclaration>>();
			HashMap<String, HashMap<String, MethodDeclaration>> predDeclare = versions.get(pred.versionBelongs.versionRepoID);
			HashMap<String, HashMap<String, MethodDeclaration>> nextDeclare = versions.get(refactoredClass.nextVersionRepoID);
			for (String path : nextDeclare.keySet()) {
				String predPath = path.replace("\\" + refactoredClass.nextVersionRepoID + "\\", "\\" + pred.versionBelongs.versionRepoID + "\\");
				HashMap<String, MethodDeclaration> predtmp = predDeclare.get(predPath);
				HashMap<String, MethodDeclaration> nexttmp = nextDeclare.get(path);
				if (predtmp == null)
					add.put(path, nexttmp);
				else {
					HashMap<String, MethodDeclaration> opt = new HashMap<String, MethodDeclaration>();
					for (String key : nexttmp.keySet()) {
						if (!predtmp.containsKey(key)) {
							opt.put(key, nexttmp.get(key));
						}
					}
					add.put(path, opt);
				}
			}
			MyFragment common = null;
			for (String name : methodInvokeByFragments.keySet()) {
				Vector<MyFragment> frag = methodInvokeByFragments.get(name);
				boolean flag = false;
				for (RefactoredInstance i : refactoredInstanceList) {
					if (i.frags.size() == frag.size()) {
						HashSet<Integer> set = new HashSet<Integer>();
						for (MyFragment f : frag) {
							int min = 2147483647;
							int which = 0;
							for (int j = 0; j < i.frags.size(); j++) {
								MyFragment ff = i.frags.elementAt(j);
								if (f.srcPath.equals(ff.srcPath) && !set.contains(j)) {
									if (ff.endLine >= f.endLine && ff.startLine <= f.startLine) {
										int t = ff.endLine - ff.startLine;
										if (t < min) {
											min = t;
											which = j;
										}
									}
								}
							}
							if (min < 2147483647)
								set.add(which);
						}
						if (set.size() == frag.size()) {
							flag = true;
							break;
						}
					}
				}
				if (flag)
					continue;
				for (String path : add.keySet()) {
					HashMap<String, MethodDeclaration> added = add.get(path);
					if (!added.containsKey(name)) 
						continue;
					MethodDeclaration method = added.get(name);
					common = new MyFragment(method.getBeginLine(), method.getEndLine(), -1, -1, path, pred.versionBelongs.versionID + 1, refactoredClass.nextVersionRepoID);
					Vector<Double> ret = calcSimilarity(methodInvokeByFragments.get(name), common);
					RefactoredInstance t = new RefactoredInstance(methodInvokeByFragments.get(name), common);
					groupsWithSimilarity.put(t, ret);
					if (ret.size() == 0)
						continue;
					Collections.sort(ret, Collections.reverseOrder());
					double max = ret.elementAt(0);
					maxSimi.put(t, max);
					if (ret.elementAt(1) > 0.3) {
						if (!refactoredInstanceList.contains(t)) {
							refactoredInstanceList.addElement(t);
							//resultCnt ++;
						}
					}
				}
			}
			if (common != null)
				method3 ++;
			
		}
		
		refactoredInstanceList = filter(refactoredInstanceList);
		PrintResult(refactoredInstanceList);
		System.out.println("method1 = " + method1);
		System.out.println("method2 = " + method2);
		System.out.println("method3 = " + method3);
		System.out.println("resultCnt = " + refactoredInstanceList.size());
		File file = new File("D:\\gztest2\\d.txt");
		FileWriter w = new FileWriter(file);
		Map<RefactoredInstance, Double> tmp = MapUtil.sortByValue(maxSimi);
		for (RefactoredInstance c : tmp.keySet()) {
			Vector<Double> t = groupsWithSimilarity.get(c);
			w.write("\r\nMaxSimi: " + tmp.get(c) + "\n");
			w.write("Simis are:\n");
			for (int i = 0; i < t.size(); i++) {
				w.write(t.elementAt(i) + "\n");
			}
			w.write(c.toString());
		}
		w.write("\n");
		w.close();
	}
	
	private static Vector<RefactoredInstance> filter(Vector<RefactoredInstance> refactoredInstanceList) {
		Vector<RefactoredInstance> ret = new Vector<RefactoredInstance>();
		for (int i = 0; i < refactoredInstanceList.size(); i++) {
			Vector<MyFragment> fragi = refactoredInstanceList.elementAt(i).frags;
			boolean flag = false;
			for (int j = i + 1; j < refactoredInstanceList.size(); j++) {
				Vector<MyFragment> fragj = refactoredInstanceList.elementAt(j).frags;
				if (fragi.size() == fragj.size()) {
					HashSet<Integer> set = new HashSet<Integer>();
					for (MyFragment f : fragi) {
						int min = 2147483647;
						int which = 0;
						for (int jj = 0; jj < fragj.size(); jj++) {
							MyFragment ff = fragj.elementAt(jj);
							if (f.srcPath.equals(ff.srcPath) && !set.contains(jj)) {
								if (ff.endLine >= f.endLine && ff.startLine <= f.startLine) {
									int t = ff.endLine - ff.startLine;
									if (t < min) {
										min = t;
										which = jj;
									}
								}
							}
						}
						if (min < 2147483647)
							set.add(which);
					}
					if (set.size() == fragi.size()) {
						flag = true;
						break;
					}
				}
			}
			if (!flag)
				ret.addElement(refactoredInstanceList.elementAt(i));
		}
		return ret;
	}

	private static void getMethodDeclaration(String basePath) {
		String version = basePath.substring(basePath.lastIndexOf("\\") + 1);
		HashMap<String, HashMap<String, MethodDeclaration>> tmp = new HashMap<String, HashMap<String, MethodDeclaration>>();
		versions.put(version, tmp);
		
		File file = new File(basePath);
		File[] files = file.listFiles();
		for (File tmpFile : files) {
			judge(tmpFile, version);
		}
	}
	
	public static void judge(File tmpFile, String version) {
		if (tmpFile.isDirectory()) {
			File[] files = tmpFile.listFiles();
			for (File file : files) {
				judge(file, version);
			}
		}
		else {
			if (!tmpFile.getName().endsWith(".java")) {
				return;
			}
			else {
				HashMap<String, HashMap<String, MethodDeclaration>> tmp = versions.get(version);
				MethodVisitor v = null;
				try{
					FileInputStream in = new FileInputStream(tmpFile.getAbsolutePath());
					CompilationUnit cu = JavaParser.parse(in);
					v = new MethodVisitor();
					v.visit(cu, null);
				}
				catch (Exception ex) {
					return;
				}
				catch (Error err) {
					System.out.println("Error happened: \n" + tmpFile.getAbsolutePath());
					return;
				}
				tmp.put(tmpFile.getAbsolutePath(), v.methodDeclare);
			}
		}
	}
	
	private static void PrintResult(Vector<RefactoredInstance> t) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(refactoredOutputPath)));
			oos.writeObject(t);
			oos.close();
			
			FileOutputStream fos = new FileOutputStream(new File(refactoredOutputReadablePath));
			fos.write(t.toString().getBytes());
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Vector<Double> calcSimilarity(Vector<MyFragment> frags, MyFragment common) {
		Vector<Double> ret = new Vector<Double>();
		GZTokenVisitor commonv = null;
		try {
			FileInputStream in = new FileInputStream(common.srcPath);
			CompilationUnit cu = JavaParser.parse(in);
			commonv = new GZTokenVisitor();
			commonv.visit(cu, null);
		} catch (Exception ex) {
			return ret;
		}
		catch (Error err) {
			System.out.println("Error happened: \n" + common.srcPath);
			return ret;
		}
		Vector<String> methodContent = new Vector<String>();
		for (int i = common.startLine; i <= common.endLine; i++) {
			Vector<String> t = commonv.tokens.get(i);
			if (t == null || t.size() == 0)
				continue;
			else {
				for (int j = 0; j < t.size(); j++)
					methodContent.addElement(t.elementAt(j));
			}
		}
		int methodLen = methodContent.size();
		int fragLen = 0;
		for (int ii = 0; ii < frags.size(); ii++) {
			MyFragment frag = frags.elementAt(ii);
			String predPath = frag.srcPath;
			String curPath = predPath.replace("\\" + frag.versionRepoID + "\\", "\\" + (Integer.parseInt(frag.versionRepoID)-1) + "\\");
			int[] linemap = buildLineMap(curPath, predPath);
			HashSet<Integer> lineSet = new HashSet<Integer>();
			if (linemap != null) {
				for (int i = frag.startLine; i <= frag.endLine; i++) {
					if (linemap[i-1] == -1)
						lineSet.add(i);
				}
			}
			else {
				for (int i = frag.startLine; i <= frag.endLine; i++) {
					lineSet.add(i);
				}
			}
			GZTokenVisitor fragv = null;
			try {
				FileInputStream in = new FileInputStream(frag.srcPath);
				CompilationUnit cu = JavaParser.parse(in);
				fragv = new GZTokenVisitor();
				fragv.visit(cu, null);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			catch (Error err) {
				System.out.println("Error happened: \n" + frag.srcPath);
				continue;
			}
			
			Vector<String> fragContent = new Vector<String>();
			for (int i = frag.startLine; i <= frag.endLine; i++) {
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
			ret.addElement(1 - (double)distance*2/(methodLen + fragLen));
		}
		
		return ret;
	}
	
	private static Vector<String> detectAddedMethodInvoke(MyFragment frag, String nextVersionRepoID) throws Exception {
		String predPath = frag.srcPath;
		String nextPath = predPath.replace("\\" + frag.versionRepoID + "\\", "\\" + nextVersionRepoID + "\\");
		
		int[] linemap = buildLineMap(nextPath, predPath);
		if (linemap == null) {
			return null;
		}
		if (linemap.length <= frag.startLine - 1) {
			System.out.println("Wrong here. frag:\n" + frag);
			System.out.println("linemap:\n" + linemap.length);
		}
        int nextStart = linemap[frag.startLine - 1] + 1;
        if (linemap[frag.startLine - 1] == -1) {
        	for (int i = frag.startLine - 1; i <= frag.endLine - 1; i++)
        		if (linemap[i] != -1) { nextStart = linemap[i]; break; }
        }
        int nextEnd = linemap[frag.endLine - 1] + 1;
        if (linemap[frag.endLine - 1] == -1) {
        	for (int i = frag.endLine - 1; i >= frag.endLine - 1; i--)
        		if (linemap[i] != -1) { nextEnd = linemap[i]; break; }
        }
		if (nextStart == 0 || nextEnd == 0)
			return null;
		MethodVisitor predv = null;
		MethodVisitor nextv = null;
		try {
			FileInputStream in = new FileInputStream(predPath);
			CompilationUnit cu = JavaParser.parse(in);
			predv = new MethodVisitor();
			predv.visit(cu, null);
		
			in = new FileInputStream(nextPath);
			cu = JavaParser.parse(in);
			nextv = new MethodVisitor();
			nextv.visit(cu, null);
		}
		catch (Exception ex) {
			return null;
		}
		catch (Error err) {
			System.out.println("Error happened: \n" + predPath + " or " + nextPath);
			return null;
		}
		
		Vector<String> predAll = new Vector<String>();
		
		for (int i = frag.startLine; i <= frag.endLine; i++) {
			
			Vector<String> pred = predv.methodInvoke.get(i);
			if (pred == null)
				continue;
			for (String tmp : pred) {
				predAll.addElement(tmp);
			}
		}
		/*for (int i : predv.methodInvoke.keySet()) {
			Vector<String> pred = predv.methodInvoke.get(i);
			if (pred == null)
				continue;
			for (String tmp : pred) {
				predAll.addElement(tmp);
			}
		}*/
		Vector<String> add = new Vector<String>();
		
		for (int i = nextStart; i <= nextEnd; i++) {
			Vector<String> next = nextv.methodInvoke.get(i);
			if (next == null)
				continue;
			for (String tmp : next) {
				if (!predAll.contains(tmp)) {
					add.addElement(tmp);
				}
			}
		}
		/*
		for (int i : nextv.methodInvoke.keySet()) {
			Vector<String> next = nextv.methodInvoke.get(i);
			if (next == null)
				continue;
			for (String tmp : next) {
				if (!predAll.contains(tmp)) {
					add.addElement(tmp);
				}
			}
		}
		*/
		return add;
	}
	
	public static int[] buildLineMap(String curPath, String predPath) {
        
        File tempFile = new File(curPath);
        if(!tempFile.exists()){
            return null;
        }
        tempFile = new File(predPath);
        if (!tempFile.exists()){
        	return null;
        }
    
        List<String> currentValue = ChangeGenerator.getSrc(curPath);
        List<String> predValue = ChangeGenerator.getSrc(predPath);
		int[] linemap = MyVersionList.lineMapTable.get(curPath);
        if(linemap==null){
            
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
            MyVersionList.lineMapTable.put(curPath, linemap);
        }
        
        return linemap;
	}
	
}
