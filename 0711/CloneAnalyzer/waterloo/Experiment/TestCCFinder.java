package waterloo.Experiment;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import waterloo.SortByVersion;
import edu.pku.sei.codeclone.predictor.MyCloneClass;
import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.RefactoredInstance;

public class TestCCFinder {

	static String[] projectNames = {"eclipse.jdt.core", "jfreechart", "jruby", "lucene"};
	
	public static void main(String[] args) throws Exception {
		
		/*for (String projectName : projectNames) {
			//combine our
			String path1 = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/" + projectName + "/groupFeatures-our.arff";
			System.out.println(projectName);
			BufferedReader r = new BufferedReader(new FileReader(new File(path1)));
			String tmp = "";
			boolean flag = false;
			Vector<String> family = new Vector<String>();
			while ((tmp = r.readLine()) != null) {
				if (flag == true) {
					String[] opt = tmp.split(",");
					family.add(opt[2]);
				}
				if (tmp.equals("@data")) {
					flag = true;
				}
			}
			r.close();
			System.out.println(family.size());
			System.out.println(family);
			String path2 = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/" + projectName + "/groupFeatures-MonthNewloc-MonthAddNorhisClonediffNor.arff";
			r = new BufferedReader(new FileReader(new File(path2)));
			flag = false;
			Vector<String> header = new Vector<String>();
			Vector<String> features = new Vector<String>();
			int cnt = 0;
			while ((tmp = r.readLine()) != null) {
				if (flag == true) {
					String[] opt = tmp.split(",");
					String feature = "";
					for (int i = 0; i < opt.length - 1; i++) {
						if (i == 2) {
							feature += family.get(cnt) + ",";
						}
						else {
							if (i == 4) {
								feature += "0,";
							}
							else {
								feature += opt[i] + ",";
							}
						}
					}
					feature += opt[opt.length-1];
					features.add(feature);
					cnt ++;
				}
				else {
					header.add(tmp);
				}
				if (tmp.equals("@data")) {
					flag = true;
				}
			}
			System.out.println(header);
			String outpath = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/" + projectName + "/our+methodline.arff";
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(outpath)));
			for (String str : header) {
				w.write(str + "\n");
			}
			for (String str : features) {
				w.write(str + "\n");
			}
			w.close();
			
			//combine wangwei + month
			String path1 = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/" + projectName + "/groupFeatures-Month.arff";
			System.out.println(projectName);
			BufferedReader r = new BufferedReader(new FileReader(new File(path1)));
			String tmp = "";
			boolean flag = false;
			Vector<String> months = new Vector<String>();
			while ((tmp = r.readLine()) != null) {
				if (flag == true) {
					String[] opt = tmp.split(",");
					months.add(opt[4]);
				}
				if (tmp.equals("@data")) {
					flag = true;
				}
			}
			r.close();
			System.out.println(months.size());
			System.out.println(months);
			String path2 = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/" + projectName + "/groupFeatures-wangwei.arff";
			r = new BufferedReader(new FileReader(new File(path2)));
			flag = false;
			Vector<String> header = new Vector<String>();
			Vector<String> features = new Vector<String>();
			int cnt = 0;
			while ((tmp = r.readLine()) != null) {
				if (flag == true) {
					String[] opt = tmp.split(",");
					String feature = "";
					for (int i = 0; i < opt.length - 1; i++) {
						if (i == 4) {
							feature += months.get(cnt) + ",";
						}
						else {
							feature += opt[i] + ",";
						}
					}
					feature += opt[opt.length-1];
					features.add(feature);
					cnt ++;
				}
				else {
					header.add(tmp);
				}
				if (tmp.equals("@data")) {
					flag = true;
				}
			}
			System.out.println(header);
			String outpath = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/" + projectName + "/wangwei.arff";
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(outpath)));
			for (String str : header) {
				w.write(str + "\n");
			}
			for (String str : features) {
				w.write(str + "\n");
			}
			w.close();
		}*/
		
		
		
		/*for (String projectName : projectNames) {
			String cloneDataPath = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/" + projectName + "/";
			System.out.println(projectName);
			File cloneFileFolder = new File(cloneDataPath);
			ArrayList<File> refactorCloneFileList = new ArrayList<File>();
			for (File cloneFile : cloneFileFolder.listFiles()) {
				if(cloneFile.isDirectory())
					continue;
				String fileName = cloneFile.getName();
				if (fileName.contains("readable"))
					continue;
				if (fileName.contains("unrefactored")) {}
				else if (fileName.contains("refactored"))
					refactorCloneFileList.add(cloneFile);
			}
			Collections.sort(refactorCloneFileList, new SortByVersion());
			System.out.println("RefactorList:" + refactorCloneFileList);
			Vector<RefactoredInstance> refactoredInsList = new Vector<RefactoredInstance>();
			for (File cloneFile : refactorCloneFileList) {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
				Vector<RefactoredInstance> refactoredInsListtmp = (Vector<RefactoredInstance>) ois.readObject();
				ois.close();
				for (RefactoredInstance ins : refactoredInsListtmp) {
					refactoredInsList.add(ins);
				}
			}
			System.out.println("Before filter:" + refactoredInsList.size());
			
			String outputBasePath = "/home/sonia/NewExperiment/NewFolder-after6.26/codeFiles/" + projectName + "/";
			File file = new File(outputBasePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			int cnt = 0;
			for (RefactoredInstance ins : refactoredInsList) {
				cnt ++;
				String path = outputBasePath + cnt;
				file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				}
				int num = 0;
				System.out.println("Number " + cnt + ":");
				for (MyFragment frag : ins.frags) {
					num ++;
					System.out.println(frag.startLine + " " + frag.endLine);
					//String filePath = path + "/cloneInstance" + num + ".java";
					//file = new File(filePath);
					//file.createNewFile();
					//BufferedWriter w = new BufferedWriter(new FileWriter(file));
					//BufferedReader r = new BufferedReader(new FileReader(frag.getFilePath()));
					//String tmp = "";
					//int linenum = 0;
					//while ((tmp = r.readLine()) != null) {
						//linenum ++;
						if (linenum >= frag.startLine && linenum <= frag.endLine) {
							w.write(tmp+"\n");
						}
						if (linenum >= frag.endLine)
							break;
						
						//w.write(tmp + "\n");
					//}
					//r.close();
					//w.close();
				}
			}
			
		}*/
	}
	
	private static boolean isSingleGroupParserable(List<MyFragment> frags) {
		for (MyFragment frag : frags) {
			if (isParserable(frag))
				return true;
		}
		return false;
	}


	private static boolean isParserable(MyFragment frag) {
		File file = new File(frag.getFilePath());
		try {
			CompilationUnit cu = JavaParser.parse(file);
		} catch (Exception e) {
			return false;
		} catch (Error e) {
			return false;
		}
		return true;
	}
	
}
