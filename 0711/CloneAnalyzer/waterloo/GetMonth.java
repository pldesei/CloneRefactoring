package waterloo;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

import edu.pku.sei.codeclone.predictor.CloneGroupFeature;
import edu.pku.sei.codeclone.predictor.CloneInstanceFeature;
import edu.pku.sei.codeclone.predictor.History;
import edu.pku.sei.codeclone.predictor.HistoryGen;
import edu.pku.sei.codeclone.predictor.MyCloneClass;
import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.MyVersion;
import edu.pku.sei.codeclone.predictor.MyVersionList;
import edu.pku.sei.codeclone.predictor.RefactoredInstance;
import edu.pku.sei.codeclone.predictor.code.JavaClass;
import edu.pku.sei.codeclone.predictor.code.MethodVisitor;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import waterloo.Util.GlobalSettings;
import edu.pku.sei.codeclone.predictor.code.ClassNode;

public class GetMonth {
	String refactorFileLabel = "refactored";
	String unrefactorFileLabel = "unrefactored";
	String arffFileSuffix = ".arff";
	long refactoredGroupCnt = 0, unrefactoredGroupCnt = 0;

	String repoFolderPath = null, cloneDataPath = null;
	
	String projectPath = null;
	
	Vector<String> filterCommits = new Vector<String>();
	Vector<String> allCommits = new Vector<String>();
	
	HashMap<String, Date> allReponameDateTable = new HashMap<String, Date>();
	
	private HashMap<String, Date> getAllReponameDataTable(String logFilePath) {
		HashMap<String, Date> reponameDateTable = new HashMap<String, Date>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(logFilePath));
			String line = null;
			Vector<String> contents = new Vector<String>();
			while ((line = in.readLine()) != null) {
				contents.add(line);
			}
			for (int i = contents.size() - 1; i >= 0; i--) {
				String[] tmp = contents.get(i).split(",");
				String dateStr = tmp[3];// changed to new log format
				if (tmp[3].equals(" Jr")) {
					dateStr = tmp[4];
				}
				SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
				reponameDateTable.put(tmp[0], formatter.parse(dateStr));
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reponameDateTable;
	}

	private RepoStructure buidRepoStructure(String versionBasePath) {
		File objFile = new File(versionBasePath + GlobalSettings.pathSep + "repoStructure.obj");
		if(!objFile.exists())
			try {
				objFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		RepoStructure repoStructure = new RepoStructure(versionBasePath);
		repoStructure = new RepoStructure(versionBasePath);
		repoStructure.analyzeStructure();
		repoStructure.writeObj(objFile);
		return repoStructure;
	}

	public String getArffFilePath(String cloneDataPath,boolean addNewLoc) {
		String arffFilePath = cloneDataPath + GlobalSettings.pathSep + "groupFeatures-Month";
		if (addNewLoc)
			arffFilePath += "Newloc-Month";
		return arffFilePath + this.arffFileSuffix;
	}

	private PrintWriter getPrintWriter(String cloneDataPath,boolean addNewLoc) {
		String arffFilePath = this.getArffFilePath(cloneDataPath,addNewLoc);
		File arffFile = new File(arffFilePath);
		PrintWriter pw = null;
		try {
			if (!arffFile.exists())
				arffFile.createNewFile();
			pw = new PrintWriter(new FileWriter(arffFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pw;
	}

	Vector<String> getCommits(String path) {
		Vector<String> ret = new Vector<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String tmp = "";
			while ((tmp = in.readLine()) != null) {
				String[] opt = tmp.split(",");
				ret.add(opt[0]);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Vector<String> turnaround = new Vector<String>();
		for (int i = ret.size() - 1; i >= 0; i--) {
			turnaround.add(ret.get(i));
		}
		return turnaround;
	}
	
	public void extractFeatures(String logFilePath, String allLogFilePath, String repoFolderPath, 
			String cloneDataPath, String projectPath) {
		System.out.print("start:");
		outputCurrentTime();
		this.repoFolderPath = repoFolderPath;
		this.cloneDataPath = cloneDataPath;
		this.projectPath = projectPath;

		allReponameDateTable = getAllReponameDataTable(allLogFilePath);
		filterCommits = getCommits(logFilePath);
		allCommits = getCommits(allLogFilePath);
		System.out.print("afterBuildReponameDate:");
		outputCurrentTime();
		// Wei Wang
		PrintWriter pwArff = getPrintWriter(cloneDataPath,false);
		outputArffHeader(pwArff, false);
		
		// Add New Location Feature
		PrintWriter pwNewloc = getPrintWriter(cloneDataPath,true);
		outputArffHeader(pwNewloc, true);

		File cloneFileFolder = new File(cloneDataPath);
		ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
		ArrayList<File> refactorCloneFileList = new ArrayList<File>();
		for (File cloneFile : cloneFileFolder.listFiles()) {
			if(cloneFile.isDirectory())
				continue;
			String fileName = cloneFile.getName();
			if (fileName.contains("readable"))
				continue;
			if (fileName.contains(unrefactorFileLabel))
				unrefactorCloneFileList.add(cloneFile);
			else if (fileName.contains(refactorFileLabel))
				refactorCloneFileList.add(cloneFile);
		}
		Collections.sort(unrefactorCloneFileList, new SortByVersion());
		Collections.sort(refactorCloneFileList, new SortByVersion());
		System.out.println("UnrefactorList:" + unrefactorCloneFileList);
		System.out.println("RefactorList:" + refactorCloneFileList);
		
		for (File cloneFile : unrefactorCloneFileList) {
			processSingleCloneFile(cloneFile, unrefactorFileLabel, pwArff, pwNewloc);
		}
		for (File cloneFile : refactorCloneFileList) {
			processSingleCloneFile(cloneFile, refactorFileLabel, pwArff, pwNewloc);
		}
		pwArff.close();
		pwNewloc.close();

		System.out.println("refactoredGroupCnt:" + refactoredGroupCnt);
		System.out.println("unrefactoredGroupCnt:" + unrefactoredGroupCnt);
		System.out.print("end:");
		outputCurrentTime();
	}

	private void processSingleCloneFile(File cloneFile, String cloneFileLabel, PrintWriter pwArff, PrintWriter pwNewloc) {
		System.out.print("beforeProcessSingleCloneFile:" + cloneFile.getAbsolutePath() + " ");
		outputCurrentTime();

		Vector<RefactoredInstance> refactoredInsList = new Vector<RefactoredInstance>();
		List<MyCloneClass> unrefactoredCloneList = new ArrayList<MyCloneClass>();

		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
			if (cloneFileLabel.equals(refactorFileLabel))
				refactoredInsList = (Vector<RefactoredInstance>) ois.readObject();
			else
				unrefactoredCloneList = (List<MyCloneClass>) ois.readObject();
			System.out.print("afterReadCloneData:");
			System.out.println("RefactorListSize:" + refactoredInsList.size());
			System.out.println("UnrefactorListSize:" + unrefactoredCloneList.size());
			outputCurrentTime();
			int cnt = 0;
			if (cloneFileLabel.equals(refactorFileLabel)) {
				for (RefactoredInstance refactoredIns : refactoredInsList) {
					Vector<MyFragment> frags = refactoredIns.getFragments();
					processSingleGroupFeature(frags, cloneFileLabel, pwArff, pwNewloc, refactoredIns, null);
				}
			} else {
				for (MyCloneClass clazz : unrefactoredCloneList) {
					List<MyFragment> frags = clazz.getFragments();
					processSingleGroupFeature(frags, cloneFileLabel, pwArff, pwNewloc, null, clazz);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.print("afterProcessSingleCloneFile:");
		outputCurrentTime();
	}

	private void processSingleGroupFeature(List<MyFragment> frags, String cloneFileLabel, PrintWriter pwArff, PrintWriter pwNewloc,
			RefactoredInstance refactorIns, MyCloneClass unrefactorIns) {
		// RepoStructure
		String versionRepoName = frags.get(0).getVersionRepoName();
		String versionRepoPath = repoFolderPath + GlobalSettings.pathSep + versionRepoName;
		RepoStructure repoStructure = buidRepoStructure(versionRepoPath);
		// RefactoredFragments
		List<CloneInstanceFeature> cloneInsFeatureListForOneGroup = 
				buildSingleGroupInsFeatureList(repoStructure, repoFolderPath, frags, refactorIns, unrefactorIns);

		if (cloneInsFeatureListForOneGroup.size() > 0) {
			if (cloneFileLabel.equals(refactorFileLabel))
				refactoredGroupCnt++;
			else if (cloneFileLabel.equals(unrefactorFileLabel))
				unrefactoredGroupCnt++;
		}

		for (int i = 0; i < cloneInsFeatureListForOneGroup.size(); i++) {
			CloneInstanceFeature insFeature = cloneInsFeatureListForOneGroup.get(i);
			pwArff.println(insFeature.toString(false, false)+cloneFileLabel);
			pwNewloc.println(insFeature.toString(true, false)+cloneFileLabel);
			//printSingleInstance(insFeature, refactorIns, unrefactorIns);
			// Write one feature for a clone group
			break;
		}
		pwArff.flush();
		pwNewloc.flush();
	}


	private List<CloneInstanceFeature> buildSingleGroupInsFeatureList(RepoStructure repoStructure, String repoFolderPath, 
			List<MyFragment> frags, RefactoredInstance refactorIns, MyCloneClass unrefactorIns) {
		HashSet<ClassNode> classFamilies = repoStructure.classFamilies;
		List<JavaClass> repoClasses = repoStructure.classes;
		List<String> methodNames = new ArrayList<String>();
		List<String> files = new ArrayList<String>();
		List<JavaClass> currentClasses = new ArrayList<JavaClass>();

		List<CloneInstanceFeature> cloneInsFeatureListForOneGroup = new ArrayList<CloneInstanceFeature>();
		for (MyFragment frag : frags) {
			if (!isParserable(frag))
				continue;
			HistoryGen hisGen = new HistoryGen();
			History his = hisGen.getHistory(frag, allReponameDateTable, filterCommits, allCommits, this.projectPath);

			CloneInstanceFeature cloneInsFeature = new CloneInstanceFeature(frag, repoClasses, his, refactorIns,
					unrefactorIns, this.projectPath, this.repoFolderPath);
			if (cloneInsFeature.method != null)
				methodNames.add(cloneInsFeature.method.getName());
			files.add(frag.getFilePath());
			currentClasses.add(cloneInsFeature.currentClass);
			cloneInsFeatureListForOneGroup.add(cloneInsFeature);
		}
		CloneGroupFeature cloneGroupFeature = new CloneGroupFeature(frags, methodNames, files, currentClasses,
				classFamilies, cloneInsFeatureListForOneGroup, repoClasses);
		for (CloneInstanceFeature cloneInsFeature : cloneInsFeatureListForOneGroup) {
			cloneInsFeature.cloneGroupFeature = cloneGroupFeature;
		}
		return cloneInsFeatureListForOneGroup;
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

	private void outputArffHeader(PrintWriter pwC, boolean addNewLoc) {
		try {
			pwC.println("@relation cloneEval");
			pwC.println();
			// For Cloning Relationship
			pwC.println("@attribute numInstance real");
			pwC.println("@attribute minLeveDis real");
			// pwC.println("@attribute isType3 {true, false}");
			pwC.println("@attribute localOrClassFamily{true, false}");
			// For Context of Clone
			pwC.println("@attribute followControlStat {true, false}");
			pwC.println("@attribute numMonthOfFile real");
			pwC.println("@attribute numLineOfMethod real");
			pwC.println("@attribute sizeProForFragVsMethod real");
			// For Cloned Code Snippet
			pwC.println("@attribute numLineOfFrag real");
			pwC.println("@attribute numTokenOfFrag real");
			pwC.println("@attribute containControlBlock {true, false}");
			pwC.println("@attribute cycComplexity real");
			pwC.println("@attribute callPro real");
			pwC.println("@attribute arithPro real");
			pwC.println("@attribute beginControlStat {true, false}");
			// For New Location Feature
			if (addNewLoc) {
				pwC.println("@attribute sameFile {true, false}");
				pwC.println("@attribute samePkg {true, false}");
				pwC.println("@attribute sameMethod {true, false}");
			}
			pwC.println("@attribute cloneEval {refactored, unrefactored}");
			pwC.println();
			pwC.println("@data");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void outputCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(new Date()));
	}
}
