package waterloo.Experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import edu.pku.sei.codeclone.predictor.CloneInstanceFeature;
import edu.pku.sei.codeclone.predictor.MyCloneClass;
import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.RefactoredInstance;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import waterloo.SortByVersion;
import waterloo.Util.GlobalSettings;

public class B {
	
	static String cloneDataPath = "/home/ubuntu/result/ant";
	static String linePath = "/home/ubuntu/result/ant/groupFeatures.arff";
	static String disPath = "/home/ubuntu/result/ant/tmpAdd-dis-new.arff";
	
	public static void main(String[] args) throws Exception {
		
		
		File cloneFileFolder = new File(cloneDataPath);
		ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
		ArrayList<File> refactorCloneFileList = new ArrayList<File>();
		for (File cloneFile : cloneFileFolder.listFiles()) {
			if(cloneFile.isDirectory())
				continue;
			String fileName = cloneFile.getName();
			if (fileName.contains("readable"))
				continue;
			if (fileName.contains("unrefactored"))
				unrefactorCloneFileList.add(cloneFile);
			else if (fileName.contains("refactored"))
				refactorCloneFileList.add(cloneFile);
		}
		Collections.sort(unrefactorCloneFileList, new SortByVersion());
		System.out.println("RefactorList:" + refactorCloneFileList);
		Collections.sort(refactorCloneFileList, new SortByVersion());
		System.out.println("UnrefactorList:" + unrefactorCloneFileList);
		List<RefactoredInstance> inses = new Vector<RefactoredInstance>();
		List<MyCloneClass> cces = new Vector<MyCloneClass>();
		
		for (File cloneFile : refactorCloneFileList) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
				Vector<RefactoredInstance> refactoredInsList = (Vector<RefactoredInstance>) ois.readObject();
				for (RefactoredInstance ins : refactoredInsList) {
					inses.add(ins);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(inses.size());
		
		for (File cloneFile : unrefactorCloneFileList) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
				List<MyCloneClass> unrefactoredInsList = (List<MyCloneClass>) ois.readObject();
				for (MyCloneClass cc : unrefactoredInsList) {
					cces.add(cc);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(cces.size());
		
		
		Vector<Integer> satisfy = new Vector<Integer>();
		Vector<Integer> satisfyun = new Vector<Integer>();
		BufferedReader r = new BufferedReader(new FileReader(new File(linePath)));
		String tmp = "";
		int cnt = 0;
		int cntun = 0;
		while ((tmp = r.readLine()) != null) {
			if (tmp.indexOf(",") > 0) {
				String[] opt = tmp.split(",");
				if (opt[opt.length-1].equals("refactored")) {
					
					if (Integer.parseInt(opt[7]) >= 6)
						satisfy.add(cnt);
					cnt ++;
				}
				if (opt[opt.length-1].equals("unrefactored")) {
					if (Integer.parseInt(opt[7]) >= 6)
						satisfyun.add(cntun);
					cntun ++;
				}
			}
		}
		r.close();
		System.out.println("cnt:" + cnt + "\ncntun:" + cntun);
		System.out.println(satisfy.size());
		System.out.println(satisfyun.size());
		
		List<RefactoredInstance> trueinses = new Vector<RefactoredInstance>();
		List<MyCloneClass> trueinsesun = new Vector<MyCloneClass>();
		r=new BufferedReader(new FileReader(new File(disPath)));
		cnt = 0;cntun = 0;
		while ((tmp = r.readLine()) != null) {
			if (tmp.indexOf(",") > 0) {
				String[] opt = tmp.split(",");
				if (opt[0].contains("refactor") && !opt[0].contains("unrefactor")) {
					double a = Double.parseDouble(opt[1]);
					if (Math.abs(a-1)<1e-10) {
						if (satisfy.contains(cnt))
							trueinses.add(inses.get(cnt));
					}
					cnt ++;
				}
				if (opt[0].contains("unrefactor")) {
					double a = Double.parseDouble(opt[1]);
					if (Math.abs(a-1)<1e-10) {
						if (satisfyun.contains(cntun))
							trueinsesun.add(cces.get(cntun));
					}
					cntun ++;
				}
			}
		}
		System.out.println(trueinses.size());
		System.out.println(trueinsesun.size());
		
		for (int i = 0; i < trueinses.size(); i++){
			printSingleInstance(trueinses.get(i), i, cloneDataPath + "/refactorInstances");
		}
		for (int i = 0; i < trueinsesun.size(); i++){
			printSingleInstance(trueinsesun.get(i), i, cloneDataPath + "/unrefactorInstances");
		}
		
		
	}
	
	private static void printOldAndNewFile(List<MyFragment> frags, String refactorInsFolderPath) {
		for (int i = 0; i < frags.size(); i++) {
			MyFragment frag = frags.get(i);
			String oldFilePath = frag.getFilePath();
			File oldCloneFromFile = new File(oldFilePath);

			File oldCloneToFile = new File(
					refactorInsFolderPath + GlobalSettings.pathSep + "oldCloneFile" + (i + 1) + ".txt");
			copyFile(oldCloneFromFile, oldCloneToFile);
			//System.out.println(oldFilePath);

			String newFilePath = frag.getPreFilePath();
			File newCloneFromFile = new File(newFilePath);
			File newCloneToFile = new File(
					refactorInsFolderPath + GlobalSettings.pathSep + "newCloneFile" + (i + 1) + ".txt");
			copyFile(newCloneFromFile, newCloneToFile);
			//System.out.println(newFilePath);

		}
	}
	
	private static void copyFile(File source, File dest) {
		try {
			if (dest.exists())
				dest.delete();
			Files.copy(source.toPath(), dest.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void printSingleInstance(MyCloneClass refactorIns, int refactoredGroupCnt, String instancesFolderPath) {
		try {
			//String instancesFolderPath = cloneDataPath + GlobalSettings.pathSep + "Instances";
			File instancesInfoFolder = new File(instancesFolderPath);
			if (!instancesInfoFolder.exists())
				instancesInfoFolder.mkdirs();

			if (refactorIns != null) {
				String singleInstanceFolderPath = instancesFolderPath + GlobalSettings.pathSep + "unrefactor"
						+ refactoredGroupCnt;
				File singleInstanceFolder = new File(singleInstanceFolderPath);
				if (!singleInstanceFolder.exists())
					singleInstanceFolder.mkdirs();
				List<MyFragment> frags = refactorIns.getFragments();
				for (int i = 0; i < frags.size(); i++) {
					File fragFile = new File(
							singleInstanceFolderPath + GlobalSettings.pathSep + "cloneInstance" + (i + 1) + ".txt");
					PrintWriter pw = new PrintWriter(new FileWriter(fragFile));
					System.out.println(frags.size());
					pw.println(frags.get(i).toString());
					pw.close();
				}

				//printOldAndNewFile(frags, singleInstanceFolderPath);

			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void printSingleInstance(RefactoredInstance refactorIns, int refactoredGroupCnt, String instancesFolderPath) {
		try {
			//String instancesFolderPath = cloneDataPath + GlobalSettings.pathSep + "Instances";
			File instancesInfoFolder = new File(instancesFolderPath);
			if (!instancesInfoFolder.exists())
				instancesInfoFolder.mkdirs();

			if (refactorIns != null) {
				String singleInstanceFolderPath = instancesFolderPath + GlobalSettings.pathSep + "refactor"
						+ refactoredGroupCnt;
				File singleInstanceFolder = new File(singleInstanceFolderPath);
				if (!singleInstanceFolder.exists())
					singleInstanceFolder.mkdirs();
				Vector<MyFragment> frags = refactorIns.getFragments();
				for (int i = 0; i < frags.size(); i++) {
					File fragFile = new File(
							singleInstanceFolderPath + GlobalSettings.pathSep + "cloneInstance" + (i + 1) + ".txt");
					PrintWriter pw = new PrintWriter(new FileWriter(fragFile));
					pw.println(frags.get(i).toString());
					pw.close();
				}
				File commonMethodFile = new File(
						singleInstanceFolderPath + GlobalSettings.pathSep + "commonMethod.txt");
				PrintWriter pw = new PrintWriter(new FileWriter(commonMethodFile));
				pw.println(refactorIns.getCommonMethod().commonToString());
				pw.close();

				printOldAndNewFile(frags, singleInstanceFolderPath);

			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isSingleGroupParserable(List<MyFragment> frags) {
		for (MyFragment frag : frags) {
			if (isParserable(frag))
				return true;
		}
		return false;
	}


	private boolean isParserable(MyFragment frag) {
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

