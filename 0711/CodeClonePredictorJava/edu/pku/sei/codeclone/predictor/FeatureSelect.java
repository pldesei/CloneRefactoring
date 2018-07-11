package edu.pku.sei.codeclone.predictor;

import japa.parser.JavaParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class FeatureSelect {
	
	int classIndex = 0;
	String outputPath = "";
	String dataStarter = "@data", attrHeader = "@attribute";
	String arffFileHeader = "";
	String[] projects = {"axis2-java", "eclipse.jdt.core", "elasticsearch", "jfreechart", "jruby", "lucene"};
	
	public static void main(String[] args) throws Exception{
		FeatureSelect t = new FeatureSelect();		
		/*int[] a= {8, 19, 6, 12, 11, 5};
		for (int i = 0; i <= 5; i++) {
			String which = t.projects[i];
			int w = a[i];
			String filePath = "/home/sonia/NewExperiment/features/0.4/test/" + which + "/noMethodLine.arff";
			String retPath = "/home/sonia/NewExperiment/features/0.4/test/" + which + "/train-8.arff";
			String retPath2 = "/home/sonia/NewExperiment/features/0.4/test/" + which + "/test-8.arff";
			t.featureSelect(filePath, retPath, w, 1, which);
			t.featureSelect(filePath, retPath2, w, 0, which);
		}*/
		
		//t.test();
		t.testWithin();
		//t.calcWithin();
		//t.calc();
		//t.filterSimi();
	}
	
	public void testWithin() throws Exception {
		for (String projectName : projects) {
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
			
			Vector<Integer> satisfy = new Vector<Integer>();
			
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
			System.out.println("After Filter:" + satisfy.size());
			
			
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
			
			String filePath = "/home/sonia/NewExperiment/features/0.4/testForTrain/" + projectName + "/WangWei.arff";
			r = new BufferedReader(new FileReader(new File(filePath)));
			Vector<String> header = new Vector<String>();
			Vector<String> content = new Vector<String>();
			String tmp = "";
			boolean flag = false;
			while ((tmp = r.readLine()) != null) {
				if (!flag) {
					header.add(tmp);
					if (tmp.equals("@data")) {
						flag = true;
					}
				}
				else {
					content.add(tmp);
				}
			}
			r.close();
			int insNum = content.size()/2;
			System.out.println(insNum);
			
		Vector<Integer> forTest = new Vector<Integer>();
		Vector<Integer> forTrain = new Vector<Integer>();
		int m = satisfy.size() - 1;
		int oneOfTen = (m + 1)/10;
		if ((m+1) % 10 != 0)
			oneOfTen ++;
		HashSet<Integer> already = new HashSet<Integer>();
		int cnt = 0;
		while (cnt < 10) {
			cnt ++;
			forTest.clear();
			forTrain.clear();
			Random random = new Random();
			while (forTest.size() < oneOfTen && already.size() < choose.size()) {
				int ch = random.nextInt(satisfy.size());
				if (choose.contains(satisfy.get(ch)) && !already.contains(ch)) {
					forTest.add(ch);
				}
			}
			while (forTest.size() < oneOfTen) {
				int ch = random.nextInt(satisfy.size());
				if (choose.contains(satisfy.get(ch))) {
					forTest.add(ch);
				}
			}
			
			for (int i = 0; i < satisfy.size(); i++) {
				if (!forTest.contains(i)) {
					forTrain.add(i);
				}
			}
			System.out.println(forTrain.size() + " " + forTest.size());
			System.out.println(forTrain);
			System.out.println(forTest);
			
			String trainPath = "/home/sonia/NewExperiment/features/0.4/tttt/arff/" + projectName + "/wwtrainInTen1_" + cnt + ".arff";
			String testPath = "/home/sonia/NewExperiment/features/0.4/tttt/arff/" + projectName + "/wwtestInTen1_" + cnt + ".arff";
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
	
	public void calcWithin() throws Exception {
		String[] trainName = new String[projects.length];
		String[] testName = new String[projects.length];
		for (int i = 0; i < projects.length; i++) {
			outputPath = "/home/sonia/NewExperiment/features/0.4/tttt/" + projects[i] + "_wwround5_j48.txt";
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
			for (int cnt = 1; cnt <= 10; cnt ++) {
				trainName[i] = "/home/sonia/NewExperiment/features/0.4/tttt/arff/" + projects[i] + "/wwtrainInTen5_" + cnt + ".arff";
				testName[i] = "/home/sonia/NewExperiment/features/0.4/tttt/arff/" + projects[i] + "/wwtestInTen5_" + cnt + ".arff";
				w.write("*******************************" + "\n");
				w.write("Train:" + trainName[i] + "\n");
				w.write("Test :" + testName[i] + "\n");
				String trainFileName = trainName[i];
				String testFileName = testName[i];
				System.out.println(trainName[i] + "\n" + testName[i]);
				DataSource atf;
				File trainArffFile = new File(trainFileName);
				File testArffFile = new File(testFileName);
				this.classIndex = 0;
				this.initArffHeaderAndClassIndex(trainFileName);
				try {
					atf = new DataSource(trainArffFile.getAbsolutePath());
					Instances instancesTrain = atf.getDataSet();
					instancesTrain.setClassIndex(classIndex);
					atf = new DataSource(testArffFile.getAbsolutePath());
					Instances instancesTest = atf.getDataSet();
					instancesTest.setClassIndex(classIndex);
					//Classifier adaboost = buildAdaBoostML(instancesTrain);
					//evaluationWithTestset(adaboost, instancesTrain, instancesTest, w);

					Classifier decisionTree = buildDecisionTree(instancesTrain);
					evaluationWithTestset(decisionTree, instancesTrain, instancesTest, w);

					//Classifier svm = buildSVM(instancesTrain);
					//evaluationWithTestset(svm, instancesTrain, instancesTest, w);

					//Classifier randomForest = buildRandomForest(instancesTrain);
					//evaluationWithTestset(randomForest, instancesTrain, instancesTest, w);
					
					//Classifier naiveBayes = buildNaiveBayes(instancesTrain);
					//evaluationWithTestset(naiveBayes, instancesTrain, instancesTest, w);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("ClassIndex:" + this.classIndex);
			}
			w.close();
		}
	}
	
	
	public void calc() throws Exception{
		String[] trainName = new String[projects.length];
		String[] testName = new String[projects.length];
		
		for (int i = 0; i < projects.length; i++) {
			trainName[i] = "/home/sonia/NewExperiment/features/0.4/testForTrain/" + projects[i] + "/5.3train.arff";
			testName[i] = "/home/sonia/NewExperiment/features/0.4/testForTrain/" + projects[i] + "/test3.arff";
		}
		outputPath = "/home/sonia/NewExperiment/features/0.4/testWithin/a.txt";
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
			for (int i = 0; i < trainName.length; i++) {
				w.write("*******************************" + "\n");
				w.write("Train:" + trainName[i] + "\n");
				w.write("Test :" + testName[i] + "\n");
				String trainFileName = trainName[i];
				String testFileName = testName[i];
				System.out.println(trainName[i] + "\n" + testName[i]);
				DataSource atf;
				File trainArffFile = new File(trainFileName);
				File testArffFile = new File(testFileName);
				this.classIndex = 0;
				this.initArffHeaderAndClassIndex(trainFileName);
				try {
					atf = new DataSource(trainArffFile.getAbsolutePath());
					Instances instancesTrain = atf.getDataSet();
					instancesTrain.setClassIndex(classIndex);
					atf = new DataSource(testArffFile.getAbsolutePath());
					Instances instancesTest = atf.getDataSet();
					instancesTest.setClassIndex(classIndex);

					//Classifier decisionTree = buildDecisionTree(instancesTrain);
					//evaluationWithTestset(decisionTree, instancesTrain, instancesTest, w);

					Classifier adaboost = buildAdaBoostML(instancesTrain);
					evaluationWithTestset(adaboost, instancesTrain, instancesTest, w);

				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("ClassIndex:" + this.classIndex);
			
		}
			w.close();
	}
	
	public void test() throws Exception{
		for (String projectName : projects) {
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
				
				Vector<Integer> satisfy = new Vector<Integer>();
				
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
				System.out.println("After Filter:" + satisfy.size());
				
				
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
				
			Vector<Integer> forTest = new Vector<Integer>();
			Vector<Integer> forTrain = new Vector<Integer>();
			int m = satisfy.size() - 1;
			
			while ( ((double)forTest.size()) / 7 * 3< (m+1)) {
				int which = satisfy.get(m);
				if (choose.contains(which)) {
					forTest.add(m);
				}
				m--;
			}
			for (int i = 0; i <= m; i++) {
				forTrain.add(i);
			}
			System.out.println(forTrain.size() + " " + forTest.size());
			
			String filePath = "/home/sonia/NewExperiment/features/0.4/testForTrain/" + projectName + "/noMethodLine.arff";
			r = new BufferedReader(new FileReader(new File(filePath)));
			Vector<String> header = new Vector<String>();
			Vector<String> content = new Vector<String>();
			String tmp = "";
			boolean flag = false;
			while ((tmp = r.readLine()) != null) {
				if (!flag) {
					header.add(tmp);
					if (tmp.equals("@data")) {
						flag = true;
					}
				}
				else {
					content.add(tmp);
				}
			}
			r.close();
			int insNum = content.size()/2;
			System.out.println(insNum);
			System.out.println(forTrain);
			System.out.println(forTest);
			String trainPath = "/home/sonia/NewExperiment/features/0.4/testForTrain/" + projectName + "/train3.arff";
			String testPath = "/home/sonia/NewExperiment/features/0.4/testForTrain/" + projectName + "/test3.arff";
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
			//for (int ii = content.size() - 1; ii >= content.size()- forTrain.size(); ii--) {
			//	w.write(content.get(ii) + "\n");
			//}
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
			//for (int ii = insNum; ii < forTest.size(); ii++) {
			//	w.write(content.get(ii) + "\n");
			//}
			w.close();
		}
		
	}
	
	public void featureSelect(String filePath, String retPath, int num, int deleteOrReserve, String projectName) throws Exception {
		BufferedReader r = new BufferedReader(new FileReader(new File(filePath)));
		Vector<String> header = new Vector<String>();
		Vector<String> content = new Vector<String>();
		String tmp = "";
		boolean flag = false;
		while ((tmp = r.readLine()) != null) {
			if (!flag) {
				header.add(tmp);
				if (tmp.equals("@data")) {
					flag = true;
				}
			}
			else {
				content.add(tmp);
			}
		}
		r.close();
		int insNum = content.size()/2;
		System.out.println(insNum);
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(retPath)));
		for (String str : header) {
			w.write(str + "\n");
		}
		if (deleteOrReserve == 1) {
			//Delete
			for (int i = 1; i <= content.size(); i++) {
				/*if (i <= insNum - num || (i > insNum && i <= content.size() - num)) {
					w.write(content.get(i-1) + "\n");
				}*/
				if (i <= insNum - num || i >= content.size() - num) {
					w.write(content.get(i - 1) + "\n");
				}
			}
		}
		else {
			//Reserve
			for (int i = 1; i <= content.size(); i++) {
				/*if (i >= content.size() - num + 1|| (i <= insNum && i >= insNum - num + 1)) {
					w.write(content.get(i-1) + "\n");
				}*/
				if ((i <= insNum && i >= insNum - num + 1) || (i > insNum && i <= insNum + num)) {
					w.write(content.get(i-1) + "\n");
				}
			}
			/*
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
			
			Vector<Integer> satisfy = new Vector<Integer>();
			
			String infoPath =  "/home/sonia/NewExperiment/ManuCheck/filter/" + projectName + "/";
			String filterFilePath = infoPath + "result";
			File filterFile = new File(filterFilePath);
			r = new BufferedReader(new FileReader(filterFile));
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
			System.out.println("After Filter:" + satisfy.size());
			
			
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
			
			if (satisfy.size() != insNum) {
				System.out.println("unequal");
			}
			
			Vector<Integer> finallychoose = new Vector<Integer>();
			for (int i = satisfy.size() - num; i < satisfy.size(); i++) {
				int thisnum = satisfy.get(i);
				if (choose.contains(thisnum)) {
					w.write(content.get(i) + "\n");
					finallychoose.add(thisnum);
				}
			}
			System.out.println(finallychoose.size());
			for (int i = content.size() - finallychoose.size(); i < content.size(); i++) {
				w.write(content.get(i) + "\n");
			}
			*/
		}
		w.close();
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
						arffFileHeader += str + "\n";
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
	
	private void evaluationWithTestset(Classifier classifier, Instances instancesTrain, Instances instancesTest, BufferedWriter w) {
		try {
			Evaluation eval = new Evaluation(instancesTrain);
			eval.evaluateModel(classifier, instancesTest);
			//System.out.println(classifier.toString());
			System.out.println(eval.toClassDetailsString());
			//System.out.println(eval.toSummaryString());
			//w.write(classifier.toString() + "\n");
			w.write(eval.toClassDetailsString() + "\n");
			//w.write(eval.toSummaryString() + "\n");
			
			try {
				ArrayList<Double> refactorPossListForTest = new ArrayList<Double>();
				System.out.println("numTestInstances:" + instancesTest.numInstances());
				for (int i = 0; i < instancesTest.numInstances(); i++) {
					Instance ins = instancesTest.instance(i);
					double[] possDis = classifier.distributionForInstance(ins);
					// class is {refactored, unrefactored}, 0 represents refactored
					// clones
					//System.out.println(possDis[0]);
					refactorPossListForTest.add(possDis[0]);
				}
				Collections.sort(refactorPossListForTest, Collections.reverseOrder());	
				for (double pos : refactorPossListForTest) {
					//System.out.println(pos);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
public int[] buildLineMap(String curPath, String predPath) {
        
        File tempFile = new File(curPath);
        if(!tempFile.exists()){
            return null;
        }
        tempFile = new File(predPath);
        if (!tempFile.exists()){
        	return null;
        }
    
		int[] linemap = MyVersionList.lineMapTable.get(curPath);
        if(linemap==null){
        	List<String> currentValue = ChangeGenerator.getSrc(curPath);
            List<String> predValue = ChangeGenerator.getSrc(predPath);
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

public void filterSimi() throws Exception {
	for (String projectName : projects) {
		if (projectName.equals("axis2-java") || projectName.equals("elasticsearch"))
			continue;
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
		
		Vector<Integer> satisfy = new Vector<Integer>();
		
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
		System.out.println("After Filter:" + satisfy.size());
		
		Vector<Integer> f = new Vector<Integer>();
		for (int ii = 0; ii < satisfy.size(); ii++) {
			RefactoredInstance ins = refactoredInsList.get(ii);
			Vector<Double> simis = calcSimilarity(ins.frags, ins.commonMethod);
			int cnt = 0;
			for (Double s : simis) {
				if (s >= 0.1)
					cnt ++;
			}
			if (cnt < 2) {
				f.add(ii);
			}
		}
		System.out.println(f.size());
		System.out.println(f);
	}
}

public Vector<Double> calcSimilarity(Vector<MyFragment> frags, MyFragment common) {
	Vector<Double> ret = new Vector<Double>();
	AnotherTokenVisitor commonv = null;
	FileInputStream in = null;
	try {
		in = new FileInputStream(common.srcPath);
		japa.parser.ast.CompilationUnit cu = JavaParser.parse(in);
		commonv = new AnotherTokenVisitor();
		commonv.visit(cu, null);
		in.close();
	} catch (Exception ex) {
		return ret;
	}
	catch (Error err) {
		System.out.println("Error happened: \n" + common.srcPath);
		return ret;
	}
	finally {
		try {
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	System.out.println("method size:" + methodLen);
	int fragLen = 0;
	for (int ii = 0; ii < frags.size(); ii++) {
		MyFragment frag = frags.elementAt(ii);
		String predPath = frag.srcPath;
		String curPath = predPath.replace("/" + frag.versionRepoID, "/" + common.versionRepoID);
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
		AnotherTokenVisitor fragv = null;
		//FileInputStream in = null;
		try {
			in = new FileInputStream(frag.srcPath);
			japa.parser.ast.CompilationUnit cu = JavaParser.parse(in);
			fragv = new AnotherTokenVisitor();
			fragv.visit(cu, null);
			in.close();
		} catch (Exception e) {
			//e.printStackTrace();
			continue;
		}
		catch (Error err) {
			System.out.println("Error happened: \n" + frag.srcPath);
			continue;
		}
		finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		System.out.println("frag size:" + fragLen);
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
}
