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
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import edu.pku.sei.codeclone.predictor.CloneInstanceFeature;
import edu.pku.sei.codeclone.predictor.MyCloneClass;
import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.RefactoredInstance;
import waterloo.SortByVersion;
import waterloo.Experiment.TopKPrecision.NewInstance;
import waterloo.Util.GlobalSettings;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Debug.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Features {

	String dataStarter = "@data", attrHeader = "@attribute";
	int classIndex = 0;
	String arffFileHeader = "";
	String lineBreak = "\r";
	String refactorLabel = "refactored";
	String unrefactorLabel = "unrefactored";

	static String basePath;
	static String featureBasePath;
	//static String tmpBasePath;
	
	static String[] projectNames = {"axis2-java", "eclipse.jdt.core", "elasticsearch", "jfreechart", "jruby", "lucene"};
	String[] features = {"crecFeature", "wangwei", "noCodeFeature", "noHistoryFeature", "noLocationFeature", "noDiffFeature", "noCoChangeFeature"};
	static String collectName = "collectedCloneData";
	static String confirmName = "confirmedRefactoredClones";
	static String suffix = ".arff";
	int[] insNum = {43, 106, 33, 59, 65, 27};

	public static void main(String[] args) throws Exception {
		System.out.println("start");
		Features t = new Features();
		basePath = "/home/sonia/NewExperiment/NewFolder-after6.26/";
		featureBasePath = basePath + "features/";
		//t.getOurFeature();
		//t.getDiffFeature();
		t.combineFiles();
		
		
		System.out.println("end");
	}
	
	public void getOurFeature() throws Exception {
		String folderPath = featureBasePath + "unrefactorInstancesInPaper/";
		for (int i = 0; i < projectNames.length; i++) {
			String projectName = projectNames[i];
			String path = folderPath + projectName + "/our+methodline.arff";
			String retPath = folderPath + projectName + "/crecFeature.arff";
			int[] nouse = {5, 6};
			deleteFeatures(arrayToVector(nouse), path, retPath);
		}
	}
	
	public void getDiffFeature() throws Exception {
		String folderPath = featureBasePath + "unrefactorInstancesInPaper/";
		for (int i = 0; i < projectNames.length; i++) {
			String projectName = projectNames[i];
			String path = folderPath + projectName + "/crecFeature.arff";
			int[] code = {4, 5, 6, 7, 8, 9, 10, 11, 12, 16, 17};
			int[] cochange = {25, 26, 27, 28, 29};
			int[] his = {19, 20, 21, 22, 23, 24};
			int[] diff = {1, 30, 31, 32, 33, 34};
			int[] location = {2, 3, 13, 14, 15, 18};
			
			String codePath = folderPath + projectName + "/noCodeFeature.arff";
			String cochangePath = folderPath + projectName + "/noCoChangeFeature.arff";
			String hisPath = folderPath + projectName + "/noHistoryFeature.arff";
			String diffPath = folderPath + projectName + "/noDiffFeature.arff";
			String locPath = folderPath + projectName + "/noLocationFeature.arff";
			deleteFeatures(arrayToVector(code), path, codePath);
			deleteFeatures(arrayToVector(cochange), path, cochangePath);
			deleteFeatures(arrayToVector(his), path, hisPath);
			deleteFeatures(arrayToVector(diff), path, diffPath);
			deleteFeatures(arrayToVector(location), path, locPath);
		}
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
	
	private void testWithin() throws IOException {
		for (int i = 0; i < projectNames.length; i++) {
			File collect = new File(featureBasePath + projectNames[i] + "/" + collectName + suffix);
			BufferedReader r = new BufferedReader(new FileReader(collect));
			String tmp;
			HashMap<String, Integer> contents = new HashMap<String, Integer>();
			Vector<String> content = new Vector<String>();
			boolean flag = false;
			Vector<String> header = new Vector<String>();
			int cnt = 0;
			while ((tmp = r.readLine()) != null) {
				if (!flag) {
					header.add(tmp);
					if (tmp.equals("@data")) {
						flag = true;
					}
				}
				else {
					contents.put(tmp, cnt++);
					content.add(tmp);
				}
			}
			r.close();
			int insNum = contents.size()/2;
			System.out.println(insNum);
			
			Vector<Integer> choose = new Vector<Integer>();
			File confirm  = new File(featureBasePath + projectNames[i] + "/" + confirmName + suffix);
			r = new BufferedReader(new FileReader(confirm));
			while ((tmp = r.readLine()) != null) {
				if (!flag) {
					if (tmp.equals("@data")) {
						flag = true;
					}
				}
				else {
					choose.add(contents.get(tmp));
				}
			}
			r.close();
			
			Vector<Integer> forTest = new Vector<Integer>();
			Vector<Integer> forTrain = new Vector<Integer>();
			int m = content.size() - 1;
			int oneOfTen = (m + 1)/10;
			if ((m+1) % 10 != 0)
				oneOfTen ++;
			HashSet<Integer> already = new HashSet<Integer>();
			cnt = 0;
			while (cnt < 10) {
				cnt ++;
				forTest.clear();
				forTrain.clear();
				Random random = new Random();
				while (forTest.size() < oneOfTen && already.size() < choose.size()) {
					int ch = random.nextInt(content.size());
					if (choose.contains(ch) && !already.contains(ch)) {
						forTest.add(ch);
					}
				}
				while (forTest.size() < oneOfTen) {
					int ch = random.nextInt(content.size());
					if (choose.contains(ch)) {
						forTest.add(ch);
					}
				}
				
				for (int ii = 0; ii < content.size(); ii++) {
					if (!forTest.contains(ii)) {
						forTrain.add(ii);
					}
				}
				System.out.println(forTrain.size() + " " + forTest.size());
				//System.out.println(forTrain);
				//System.out.println(forTest);
				
				String trainPath = basePath + "tmp/testWithinProjects/"+ projectNames[i] + "/trainInTen_" + cnt + ".arff";
				String testPath = basePath + "tmp/testWithinProjects/" + projectNames[i] + "/testInTen_" + cnt + ".arff";
				BufferedWriter w = new BufferedWriter(new FileWriter(new File(trainPath)));
				for (String sstr : header) {
					w.write(sstr + "\n");
				}
				for (int ii : forTrain) {
					w.write(content.get(ii) + "\n");
				}
				for (int ii : forTrain) {
					w.write(content.get(ii + insNum) + "\n");
				}
				w.close();
				
				w = new BufferedWriter(new FileWriter(new File(testPath)));
				for (String sstr : header) {
					w.write(sstr + "\n");
				}
				for (int ii : forTest) {
					w.write(content.get(ii) + "\n");
				}
				for (int ii : forTest) {
					w.write(content.get(ii + insNum) + "\n");
				}
				w.close();
			}
		}
	}
	
	private Vector<Integer> arrayToVector(int[] a) {
		Vector<Integer> ret = new Vector<Integer>();
		for (int i : a) {
			ret.add(i);
		}
		return ret;
	}
	
	private void resolveFeatures(Vector<Integer> resolvenums, String originPath, String retPath) throws Exception{
		BufferedReader r = new BufferedReader(new FileReader(new File(originPath)));
		String tmp = "";
		Vector<String> header = new Vector<String>();
		Vector<String> content = new Vector<String>();
		boolean flag = false;
		int featureCnt = 0;
		while ((tmp = r.readLine()) != null) {
			if (!flag) {
				if (tmp.startsWith("@attribute")) {
					featureCnt ++;
					if (resolvenums.contains(featureCnt)) {
						header.add(tmp);
					}
				}
				else {
					header.add(tmp);
				}
			}
			else {
				String[] opt = tmp.split(",");
				String ret = "";
				for (int i = 0; i < opt.length; i++) {
					if (resolvenums.contains(i+1)) {
						ret += opt[i] + ",";
					}
				}
				ret = ret.substring(0, ret.length()-1);
				//System.out.println(ret);
				content.add(ret);
			}
			if (tmp.equals("@data")) {
				flag = true;
			}
		}
		r.close();
		
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(retPath)));
		for (String str : header)
			w.write(str + "\n");
		for (String str : content)
			w.write(str + "\n");
		w.close();
		
	}
	
	private void deleteFeatures(Vector<Integer> deletenums, String originPath, String retPath) throws Exception{
		BufferedReader r = new BufferedReader(new FileReader(new File(originPath)));
		String tmp = "";
		Vector<String> header = new Vector<String>();
		Vector<String> content = new Vector<String>();
		boolean flag = false;
		int featureCnt = 0;
		while ((tmp = r.readLine()) != null) {
			if (!flag) {
				if (tmp.startsWith("@attribute")) {
					featureCnt ++;
					if (!deletenums.contains(featureCnt)) {
						header.add(tmp);
					}
				}
				else {
					header.add(tmp);
				}
			}
			else {
				String[] opt = tmp.split(",");
				String ret = "";
				for (int i = 0; i < opt.length; i++) {
					if (!deletenums.contains(i+1)) {
						ret += opt[i] + ",";
					}
				}
				ret = ret.substring(0, ret.length()-1);
				//System.out.println(ret);
				content.add(ret);
			}
			if (tmp.equals("@data")) {
				flag = true;
			}
		}
		r.close();
		
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(retPath)));
		for (String str : header)
			w.write(str + "\n");
		for (String str : content)
			w.write(str + "\n");
		w.close();
		
	}
	
	
	public String[] combineFiles() throws Exception{
		String testForTrainPath = featureBasePath + "testForTrain/";
		for (String feature : features) {
			for (int i = 0; i < projectNames.length; i++) {
				String path1 = featureBasePath + "refactorInstancesInPaper/" + projectNames[i] + "/" + feature + suffix;
				String path2 = featureBasePath + "unrefactorInstancesInPaper/" + projectNames[i] + "/" + feature + suffix;
				String retPath = testForTrainPath + projectNames[i] + "/" + feature + suffix;
				combineTwoForOne(path1, path2, retPath);
			}
		}
		
		String trainPath = featureBasePath + "train/";
		String[] trainPaths = new String[projectNames.length];
		for (String feature : features) {
			for (int i = 0; i < projectNames.length; i++) {
				trainPaths[i] = null;
				for (int j = 0; j < projectNames.length; j ++) {
					if (i == j) {
						continue;
					}
					String path = testForTrainPath + projectNames[j] + "/" + feature + suffix;
					if (trainPaths[i] == null) {
						trainPaths[i] = combineTwoForOne(null, path, trainPath + "no-" + projectNames[i] + "-" + feature + suffix);
					}
					else {
						combineTwoForOne(trainPaths[i], path, trainPaths[i]);
					}
				}
			}
		}
		
		String testPath = featureBasePath + "test/";
		for (String feature : features) {
			for (int i = 0; i < projectNames.length; i++) {
				String path = testForTrainPath + projectNames[i] + "/" + feature + suffix;
				String afterCheckPath = testPath + projectNames[i] + "/" + feature + suffix;
				String checkPath = featureBasePath + "refactorInstancesInPaper/" + projectNames[i] + "/confirmed.txt";
				Vector<Integer> satisfy = new Vector<Integer>();
				BufferedReader r = new BufferedReader(new FileReader(new File(checkPath)));
				String tmp = "";
				while ((tmp = r.readLine()) != null) {
					String[] opt = tmp.split(" ");
					for (String str : opt) {
						satisfy.add(Integer.parseInt(str));
					}
				}
				r.close();
				
				r = new BufferedReader(new FileReader(new File(path)));
				Vector<String> header = new Vector<String>();
				Vector<String> content = new Vector<String>();
				boolean flag = false;
				int cnt = 0;
				while ((tmp = r.readLine()) != null) {
					if (flag) {
						if (satisfy.contains(cnt)) {
							content.add(tmp);
						}
						cnt ++;
						if (cnt > insNum[i] && cnt <= insNum[i] + satisfy.size()) {
							content.add(tmp);
						}
					}
					else {
						header.add(tmp);
					}
					if (tmp.equals("@data")) {
						flag = true;
					}
				}
				System.out.println(satisfy.size() + " " + content.size());
				r.close();
				
				BufferedWriter w = new BufferedWriter(new FileWriter(new File(afterCheckPath)));
				for (String str : header)
					w.write(str + "\n");
				for (String str : content)
					w.write(str + "\n");
				w.close();
				
			}
		}
		
		return trainPaths;
	}

	private String combineTwoForOne(String path1, String path2, String resultPath) throws Exception{
		//System.out.println("Path1:" + path1);
		//System.out.println("Path2:" + path2);
		//System.out.println("Result:" + resultPath);
		if (path1 == null) {
			File file = new File(path2);
			BufferedReader r = new BufferedReader(new FileReader(file));
			Vector<String> contents = new Vector<String>();
			String tmp = null;
			while ((tmp = r.readLine()) != null) {
				contents.add(tmp);
			}
			r.close();
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(resultPath)));
			for (String str : contents) {
				w.write(str + "\n");
			}
			w.close();
		}
		else {
			File file1 = new File(path1);
			BufferedReader r = new BufferedReader(new FileReader(file1));
			Vector<String> contents = new Vector<String>();
			String tmp = null;
			while ((tmp = r.readLine()) != null) {
				contents.add(tmp);
			}
			r.close();

			File file2 = new File(path2);
			r = new BufferedReader(new FileReader(file2));
			boolean flag = false;
			while ((tmp = r.readLine()) != null) {
				if (flag) {
					contents.add(tmp);
				}
				if (tmp.equals("@data")) {
					flag = true;
				}
			}

			BufferedWriter w = new BufferedWriter(new FileWriter(new File(resultPath)));
			for (String str : contents) {
				w.write(str + "\n");
			}
			w.close();

		}
		return resultPath;
	}
	
	public void initArffHeaderAndClassIndex(String arffFilePath) {
		try {
			String str = null;
			boolean start = false;
			File arffFile = new File(arffFilePath);

			BufferedReader br = new BufferedReader(new FileReader(arffFile));
			while ((str = br.readLine()) != null) {
				if (!start) {
					if (!arffFileHeader.contains(dataStarter))
						arffFileHeader += str + lineBreak;
					if (str.startsWith(attrHeader))
						this.classIndex++;
				}
				if (str.startsWith(dataStarter))
					start = true;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.classIndex--;
	}

	private Classifier buildDecisionTree(Instances instancesTrain) {
		Classifier classifier = null;
		try {
			classifier = (Classifier) Class.forName("weka.classifiers.trees.J48").newInstance();
			classifier.setOptions(weka.core.Utils.splitOptions("weka.classifiers.trees.J48 -C 0.25 -M 2"));
			classifier.buildClassifier(instancesTrain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
	}

	private Classifier buildSVM(Instances instancesTrain) {
		Classifier classifier = null;
		try {
			classifier = (Classifier) Class.forName("weka.classifiers.functions.SMO").newInstance();
			classifier.setOptions(weka.core.Utils.splitOptions(
					"weka.classifiers.functions.SMO -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
			classifier.buildClassifier(instancesTrain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;

	}
	
	private Classifier buildRandomForest(Instances instancesTrain) {
		Classifier classifier = null;
		try {
			classifier = (Classifier) Class.forName("weka.classifiers.trees.RandomForest").newInstance();
			//System.out.println(weka.core.Utils.splitOptions("weka.classifiers.trees.RandomForest -I 100 -K 0 -S 1"));
			classifier.setOptions(weka.core.Utils.splitOptions("-I 100 -K 0 -S 1"));
			classifier.buildClassifier(instancesTrain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
	}
	
	private Classifier buildNaiveBayes(Instances instancesTrain) {
		Classifier classifier = null;
		try {
			classifier = (Classifier) Class.forName("weka.classifiers.bayes.NaiveBayes").newInstance();
			String opt = "";
			classifier.setOptions(weka.core.Utils.splitOptions(opt));
			classifier.buildClassifier(instancesTrain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
	}

	private Classifier buildAdaBoostML(Instances instancesTrain) {
		Classifier classifier = null;
		try {
			classifier = (Classifier) Class.forName("weka.classifiers.meta.AdaBoostM1").newInstance();
			classifier.setOptions(weka.core.Utils.splitOptions("weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I 50 -W weka.classifiers.trees.DecisionStump"));
			classifier.buildClassifier(instancesTrain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
	}
	
	public void testCodes() {
		// base on all features(contain methodLine)
				/*int[] crec = {6};
				int[] code = {4,7,8,9,10,11,12,13,14,18,19};
				int[] history = {21,22,23,24,25,26};
				int[] location = {2,3,15,16,17,20};
				int[] diff = {1,32,34,33,35,36};
				int[] cochange = {27,28,29,30,31};
				int[] wangwei = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,37};
				*/
				/*int[] insNum = {43, 106, 33, 59, 65, 27};
				for (int i = 0; i < projectNames.length; i++) {
					if (i != 4)
						continue;
					String projectName = projectNames[i];
					String cloneDataPath = "/home/sonia/NewExperiment/features/unrefactorInstances/" + projectName + "/";
					System.out.println(projectName);
					File cloneFileFolder = new File(cloneDataPath);
					ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
					for (File cloneFile : cloneFileFolder.listFiles()) {
						if(cloneFile.isDirectory())
							continue;
						String fileName = cloneFile.getName();
						if (fileName.contains("readable"))
							continue;
						if (fileName.contains("unrefactored")) {
							unrefactorCloneFileList.add(cloneFile);
						}
					}
					System.out.println("UnRefactorList:" + unrefactorCloneFileList);
					List<MyCloneClass> unrefactoredInsList = new Vector<MyCloneClass>();
					for (File cloneFile : unrefactorCloneFileList) {
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
						List<MyCloneClass> unrefactoredInsListtmp = (List<MyCloneClass>) ois.readObject();
						ois.close();
						for (MyCloneClass mcc : unrefactoredInsListtmp) {
							unrefactoredInsList.add(mcc);
						}
					}
					System.out.println("Before filter:" + unrefactoredInsList.size());
					int number = insNum[i];
					int cnt = 0;
					List<MyCloneClass> ret = new Vector<MyCloneClass>();
					int j = 0;
					for (j = 0; j < unrefactoredInsList.size(); j++) {
						List<MyFragment> frags = unrefactoredInsList.get(j).getFragments();
						if (!isSingleGroupParserable(frags)) {
							continue;
						}
						cnt ++;
						if (cnt <= number + 2)
							ret.add(unrefactoredInsList.get(j));
						if (cnt == number + 2)
							break;
					}
					System.out.println("After filter:" + ret.size());
					System.out.println(j);
					List<MyCloneClass> rettmp = new Vector<MyCloneClass>();
					for (j = 0; j < ret.size(); j++) {
						if (j == 48 || j == 57) {}
						else {
							rettmp.add(ret.get(j));
						}
					}
					ret = rettmp;
					System.out.println("After filter:" + ret.size());
					String outpath = "/home/sonia/NewExperiment/NewFolder-after6.26/unrefactorInstancesInPaper/" + projectName + "/unrefactored1-5000.txt";
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(outpath)));
					oos.writeObject(ret);
					oos.close();
				}*/
				/*for (String projectName : projectNames) {
					String cloneDataPath = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/withoutFilter0.1Frags/" + projectName + "/";
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
					Vector<RefactoredInstance> insret = new Vector<RefactoredInstance>();
					for (int j = 0; j < refactoredInsList.size(); j++) {
						RefactoredInstance ins = refactoredInsList.get(j);
						Vector<MyFragment> frags = new Vector<MyFragment>();
						Vector<Double> simis = new Vector<Double>();
						int cnt = 0;
						for (int ii = 0; ii < ins.simis.size(); ii++) {
							if (ins.simis.elementAt(ii) >= 0.4) {
								cnt ++;
								frags.add(ins.frags.elementAt(ii));
								simis.add(ins.simis.elementAt(ii));
							}
						}
						if (cnt >= 2) {
							RefactoredInstance tmp = new RefactoredInstance(frags, ins.getCommonMethod(), simis);
							insret.add(tmp);
							if (frags.size() != ins.frags.size()) {
								System.out.println("No." + (j+1) + " has different numbers:" + frags.size() + "/" + ins.frags.size());
							}
						}
					}
					
					String outpath = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/" + projectName + "/refactored1-5000.txt";
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(outpath)));
					oos.writeObject(insret);
					oos.close();
					
				}
				*/
				/*
				for (String projectName : projectNames) {
					String cloneDataPath = "/home/sonia/NewExperiment/features/refactorInstances/" + projectName + "/";
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

					String infoPath =  "/home/sonia/NewExperiment/ManuCheck/filter/" + projectName + "/";
					String filterFilePath = infoPath + "result";
					File filterFile = new File(filterFilePath);
					BufferedReader r = new BufferedReader(new FileReader(filterFile));
					String str = "";
					Vector<Integer> unsatisfy = new Vector<Integer>();
					while ((str = r.readLine()) != null) {
						if (str.indexOf(",") < 0) {
							continue;
						}
						else {
							str = str.substring(1, str.length()-1);
							String[] opt = str.split(", ");
							for (String s : opt) {
								unsatisfy.add(Integer.parseInt(s));
							}
						}
					}
					r.close();
					filterFile = new File(filterFilePath + "2");
					r = new BufferedReader(new FileReader(filterFile));
					while ((str = r.readLine()) != null) {
						if (str.indexOf(",") < 0) {
							continue;
						}
						else {
							str = str.substring(1, str.length()-1);
							String[] opt = str.split(", ");
							for (String s : opt) {
								unsatisfy.add(Integer.parseInt(s));
							}
						}
					}
					r.close();	
					System.out.println("Filter:" + unsatisfy.size());
					
					Vector<Integer> satisfy = new Vector<Integer>();
					for (int j = 0; j < refactoredInsList.size(); j++) {
						RefactoredInstance ins = refactoredInsList.get(j);
						
						if (unsatisfy.contains(j)) {
							continue;
						}
						
						int cnt = 0;
						for (int ii = 0; ii < ins.simis.size(); ii++) {
							if (ins.simis.elementAt(ii) >= 0.4) {
								cnt ++;
							}
						}
						if (cnt >= 2) {
							satisfy.add(j);
						}
					}
					System.out.println("After Simi Filter:" + satisfy.size());
					System.out.println(satisfy);
					
					String path = "/home/sonia/NewExperiment/ManuCheck/filter/" + projectName + "/choose";
					String s = "";
					Vector<Integer> choose = new Vector<Integer>();
					BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
					while ((s = reader.readLine()) != null) {
						s = s.substring(1, s.length()-1);
						String[] opt = s.split(" ");
						for (String sstr : opt) {
							if (!sstr.equals("")) {
								int which = Integer.parseInt(sstr);
								choose.add(which-1);
							}
						}
					}
					reader.close();
					System.out.println("Choose size:" + choose.size());
					System.out.println(choose);
					

					
					//add
					Vector<RefactoredInstance> satisfyRefactoredIns = new Vector<RefactoredInstance>();
					Vector<Integer> confirmed = new Vector<Integer>();
					for (int j = 0; j < refactoredInsList.size(); j++) {
						RefactoredInstance ins = refactoredInsList.get(j);
						if (satisfy.contains(j)) {
							satisfyRefactoredIns.add(ins);
						}
					}
					for (int j = 0; j < satisfy.size(); j++) {
						int thisnum = satisfy.get(j);
						if (choose.contains(thisnum)) {
							confirmed.add(j);
						}
						else {
							System.out.println(thisnum + " " + j);
						}
					}
					System.out.println("size:" + confirmed.size());
					
					String outpath = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/" + projectName + "/refactored1-5000.txt";
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(outpath)));
					oos.writeObject(satisfyRefactoredIns);
					oos.close();
					String confirmPath = "/home/sonia/NewExperiment/NewFolder-after6.26/refactorInstancesInPaper/" + projectName + "/confirmed.txt";
					BufferedWriter w = new BufferedWriter(new FileWriter(new File(confirmPath)));
					for (int number : confirmed) {
						w.write(number + " ");
					}
					w.close();
					//add end
					
			}*/
				
	}

	class NewInstance {
		public Instance ins;
		public Double poss;
		public int cloneIndex;

		public NewInstance(Instance ins, Double poss, int index) {
			this.ins = ins;
			this.poss = poss;
			this.cloneIndex=index;
		}

		public String toString() {
			return "RefactorPoss:" + this.poss /*+ "\tFeatureSet:" + this.ins.toString()*/;
		}
	}
	
}