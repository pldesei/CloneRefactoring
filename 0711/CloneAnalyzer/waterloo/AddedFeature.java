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
import waterloo.History.GroupHistoryFeature;
import waterloo.History.InstanceHistoryFeature;
import waterloo.Util.GlobalSettings;
import edu.pku.sei.codeclone.predictor.code.ClassNode;

public class AddedFeature {
	String refactorFileLabel = "refactored";
	String unrefactorFileLabel = "unrefactored";
	String arffFileSuffix = ".arff";
	long refactoredGroupCnt = 0, unrefactoredGroupCnt = 0;

	String repoFolderPath = null, cloneDataPath = null;
		
	String oldArffFilePath;
	String newArffFilePath;
	String tmpArffFilePath;
	
	public AddedFeature(String repoFolderPath, String cloneDataPath, String oldArffFilePath) {
		this.cloneDataPath = cloneDataPath;
		this.oldArffFilePath = oldArffFilePath;
		int dotIndex = oldArffFilePath.lastIndexOf(".");
		this.newArffFilePath = oldArffFilePath.substring(0, dotIndex) + "Add" + oldArffFilePath.substring(dotIndex);
		this.tmpArffFilePath = oldArffFilePath.substring(0, oldArffFilePath.lastIndexOf(GlobalSettings.pathSep) + 1)
				+ "tmpAdd" + oldArffFilePath.substring(dotIndex);
		this.repoFolderPath = repoFolderPath;
	}
	
	public String getArffFilePath() {
		return newArffFilePath;
	}
	
	public void computeAddedFeature() {
		try {
			File tmpHisArffFile = new File(this.tmpArffFilePath);
			if (!tmpHisArffFile.exists())
				tmpHisArffFile.createNewFile();
			PrintWriter pw = new PrintWriter(tmpHisArffFile);

			File cloneFileFolder = new File(cloneDataPath);
			ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
			ArrayList<File> refactorCloneFileList = new ArrayList<File>();
			for (File cloneFile : cloneFileFolder.listFiles()) {
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
			System.out.print("afterReadInstanceData:");
			outputCurrentTime();

			for (File cloneFile : unrefactorCloneFileList) {
				processSingleCloneFile(cloneFile, unrefactorFileLabel, pw);
			}
			System.out.print("afterProcessingUnRefactorClone:");
			outputCurrentTime();

			for (File cloneFile : refactorCloneFileList) {
				processSingleCloneFile(cloneFile, refactorFileLabel, pw);
			}
			System.out.print("afterProcessingRefactorClone:");
			outputCurrentTime();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSingleCloneFile(File cloneFile, String cloneFileLabel, PrintWriter pw) {
		Vector<RefactoredInstance> refactoredInsList = new Vector<RefactoredInstance>();
		List<MyCloneClass> unrefactoredCloneList = new ArrayList<MyCloneClass>();

		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
			if (cloneFileLabel.equals(refactorFileLabel))
				refactoredInsList = (Vector<RefactoredInstance>) ois.readObject();
			else
				unrefactoredCloneList = (List<MyCloneClass>) ois.readObject();
			if (cloneFileLabel.equals(refactorFileLabel)) {
				for (RefactoredInstance refactoredIns : refactoredInsList) {
					Vector<MyFragment> frags = refactoredIns.getFragments();
					computeSingleCloneGroup(frags, cloneFileLabel, pw);
					System.out.print("AfterGettingOneRefactorInsFeature:");
					this.outputCurrentTime();
				}
			} else {
				for (MyCloneClass clazz : unrefactoredCloneList) {
					List<MyFragment> frags = clazz.getFragments();
					computeSingleCloneGroup(frags, cloneFileLabel, pw);
					System.out.print("AfterGettingOneUnrefactorInsFeature:");
					this.outputCurrentTime();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void computeSingleCloneGroup(List<MyFragment> frags, String cloneFileLabel, PrintWriter pw) {
		// compute un/refactorGroupCount
		if (!isSingleGroupParserable(frags)) {
			return;
		}
		String newCloneFileLabel = null;
		if (cloneFileLabel.equals(refactorFileLabel)) {
			refactoredGroupCnt++;
			newCloneFileLabel = "refactor" + refactoredGroupCnt;
		} else if (cloneFileLabel.equals(unrefactorFileLabel)) {
			unrefactoredGroupCnt++;
			newCloneFileLabel = "unrefactor" + unrefactoredGroupCnt;
		}
		
		List<AddedInstanceFeature> cloneInsFeatureListForOneGroup = new ArrayList<AddedInstanceFeature>();
		
		for (MyFragment frag : frags) {
			if (!isParserable(frag))
				continue;
			AddedInstanceFeature cloneInsFeature = new AddedInstanceFeature(frag);
			cloneInsFeatureListForOneGroup.add(cloneInsFeature);
		}
		
		AddedGroupFeature cloneGroupFeature = new AddedGroupFeature(frags);

		for (AddedInstanceFeature feature : cloneInsFeatureListForOneGroup) {
			pw.print(newCloneFileLabel + "," + feature.toString());
			// Write one feature for a clone group
			break;
		}
		
		pw.print("," + cloneGroupFeature.treeDistance);
		pw.println();
		pw.flush();
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


	private void combineFeature(String oldArffFilePath, String tmpArffFilePath, String newArffFilePath) {
		try {
			File oldArffFile = new File(oldArffFilePath);
			File tmpArffFile = new File(tmpArffFilePath);
			BufferedReader brOld = new BufferedReader(new FileReader(oldArffFile));
			BufferedReader brTmp = new BufferedReader(new FileReader(tmpArffFile));
			File newArffFile = new File(newArffFilePath);
			if (!newArffFile.exists())
				newArffFile.createNewFile();
			PrintWriter pw = new PrintWriter(new FileWriter(newArffFile));
			String featureStr = null;
			boolean afterData = false;
			while ((featureStr = brOld.readLine()) != null) {
				// System.out.println("featureStr:"+featureStr);
				if (!afterData) {
					if (featureStr.startsWith("@attribute cloneEval {refactored, unrefactored}")) {
						pw.println("@attribute isTest {true, false}");
						pw.println("@attribute globalVars real");
						pw.println("@attribute treeDis real");
						pw.println(featureStr);
						pw.println(brOld.readLine());
						pw.println(brOld.readLine());
						afterData = true;
					} else
						pw.println(featureStr);
				} else {
					String hisFeatureStr = brTmp.readLine();
					String hisFeature = hisFeatureStr.substring(hisFeatureStr.indexOf(","));

					String oldFeatureWithoutLabel = featureStr.substring(0, featureStr.lastIndexOf(","));
					String label = featureStr.substring(featureStr.lastIndexOf(","));

					String newFeatureStr = oldFeatureWithoutLabel + hisFeature + label;
					pw.println(newFeatureStr);
				}
			}
			pw.close();
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void combineFeature() {
		combineFeature(this.oldArffFilePath, this.tmpArffFilePath, this.newArffFilePath);
	}

	private void outputCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(new Date()));
	}
}
