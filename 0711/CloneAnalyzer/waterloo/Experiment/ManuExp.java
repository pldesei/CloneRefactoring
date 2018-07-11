package waterloo.Experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import waterloo.FeatureExtractor;
import waterloo.Util.GlobalSettings;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ManuExp {
	String dataStarter = "@data", attrHeader = "@attribute";
	File trainArffFile, testArffFile;
	String refactorLabel = "refactored", unrefactorLabel = "unrefactored";
	String arffFileHeader = "";
	String lineBreak = "\r";
	int classIndex = 0;

	public ManuExp(String expResFolderPath) {
		trainArffFile = new File(expResFolderPath + GlobalSettings.pathSep + "train.arff");
		testArffFile = new File(expResFolderPath + GlobalSettings.pathSep + "test.arff");
	}

	public void rewriteDataset(ArrayList<String> trainArffFilePathList, ArrayList<String> testArffFilePathList) {
		try {
			this.initArffHeaderAndClassIndex(trainArffFilePathList.get(0));

			PrintWriter pwTrain = new PrintWriter(new FileWriter(trainArffFile));
			pwTrain.print(arffFileHeader);
			this.rewriteData(trainArffFilePathList, pwTrain, "Train");
			pwTrain.close();

			PrintWriter pwTest = new PrintWriter(new FileWriter(testArffFile));
			pwTest.print(arffFileHeader);
			this.rewriteData(testArffFilePathList, pwTest, "Test");
			pwTest.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void rewriteData(ArrayList<String> arffFilePathList, PrintWriter pw, String flag) {
		int totalRefactorCnt = 0, totalUnrefactorCnt = 0;
		for (String arffFilePath : arffFilePathList) {
			System.out.println("*******************************");
			System.out.println(flag + " ArffFilePath:" + arffFilePath);
			ArrayList<String> refactorFeatureList = getFeatureList(arffFilePath, refactorLabel);
			ArrayList<String> unrefactorFeatureList = getFeatureList(arffFilePath, unrefactorLabel);
			// Write refactor into pw
			int refactorSize = refactorFeatureList.size();
			totalRefactorCnt += refactorSize;
			for (int i = 0; i < refactorSize; i++) {
				pw.println(refactorFeatureList.get(i));
			}
			System.out.println("RefactorSize in this arff:" + refactorSize);
			// Write unrefactor into pw
			int unrefactorSize = unrefactorFeatureList.size();
			totalUnrefactorCnt += unrefactorSize;
			for (int i = 0; i < unrefactorSize; i++) {
				pw.println(unrefactorFeatureList.get(i));
			}
			System.out.println("UnrefactorSize in this arff:" + unrefactorSize);
		}
		System.out.println("*******************************");
		System.out.println("Total" + flag + " RefactorCnt:" + totalRefactorCnt);
		System.out.println("Total" + flag + " UnrefactorCnt:" + totalUnrefactorCnt);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.classIndex--;
	}

	private ArrayList<String> getFeatureList(String arffFilePath, String label) {
		ArrayList<String> featureList = new ArrayList<String>();
		try {
			String str = null;
			boolean start = false;
			File arffFile = new File(arffFilePath);

			BufferedReader br = new BufferedReader(new FileReader(arffFile));
			while ((str = br.readLine()) != null) {
				if (start) {
					if (str.substring(str.lastIndexOf(",") + 1).equals(label)) {
						featureList.add(str);
					}
				}
				if (str.startsWith(dataStarter))
					start = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return featureList;
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

	private NewInstance[] rankEvaluation(Classifier classifier, Instances instancesTrain, Instances instancesTest) {
		NewInstance[] topNCloneIndexs = new NewInstance[GlobalSettings.topN];
		for (NewInstance cloneIndex : topNCloneIndexs)
			cloneIndex = null;
		try {
			Evaluation eval = new Evaluation(instancesTrain);
			eval.evaluateModel(classifier, instancesTest);
			ArrayList<NewInstance> refactorPossListForTest = new ArrayList<NewInstance>();
			System.out.println("numTestInstances:" + instancesTest.numInstances());
			for (int i = 0; i < instancesTest.numInstances(); i++) {
				Instance ins = instancesTest.instance(i);
				double[] possDis = classifier.distributionForInstance(ins);
				// class is {refactored, unrefactored}, 0 represents refactored
				// clones
				if (possDis[0] > 0.5){
				NewInstance newIns = new NewInstance(instancesTest.instance(i), possDis[0],i);
				refactorPossListForTest.add(newIns);
				}
			}
			Collections.sort(refactorPossListForTest, new Comparator<NewInstance>() {
				@Override
				public int compare(NewInstance arg0, NewInstance arg1) {
					return arg0.poss.compareTo(arg1.poss);
				}
			});
			int len = refactorPossListForTest.size();
			System.out.println("more than 0.5:" + len);
			int topNCloneCnt=0;
			/*int sectionLen = (int) Math.ceil((double)len/GlobalSettings.topN);
			for (int i = 0; i < GlobalSettings.topN; i++) {
				System.out.println(refactorPossListForTest.get(len-1-i*sectionLen));
				topNCloneIndexs[topNCloneCnt]=refactorPossListForTest.get(len-1-i*sectionLen);
				topNCloneCnt++;	
			}
			*/
			for (int i = len - 1; i > len - 1 - GlobalSettings.topN; i--) {
				System.out.println(refactorPossListForTest.get(i));
				topNCloneIndexs[topNCloneCnt]=refactorPossListForTest.get(i);
				topNCloneCnt++;					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topNCloneIndexs;
	}

	public NewInstance[] predict() {
		System.out.println("*******************************");
		System.out.println("ClassIndex:" + this.classIndex);
		NewInstance[] topNCloneIndexs = new NewInstance[GlobalSettings.topN];
		for (NewInstance cloneIndex : topNCloneIndexs)
			cloneIndex = null;
		DataSource atf;
		try {
			atf = new DataSource(trainArffFile.getAbsolutePath());
			Instances instancesTrain = atf.getDataSet();
			instancesTrain.setClassIndex(classIndex);
			atf = new DataSource(testArffFile.getAbsolutePath());
			Instances instancesTest = atf.getDataSet();
			instancesTest.setClassIndex(classIndex);

			//Classifier decisionTree = buildDecisionTree(instancesTrain);
			//topNCloneIndexs=rankEvaluation(decisionTree, instancesTrain, instancesTest);

			//Classifier svm = buildSVM(instancesTrain);
			//rankEvaluation(svm, instancesTrain, instancesTest);
			
			Classifier adaboost = buildAdaBoostML(instancesTrain);
			topNCloneIndexs=rankEvaluation(adaboost, instancesTrain, instancesTest);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return topNCloneIndexs;
	}
	
	private Classifier buildAdaBoostML(Instances instancesTrain) {
		Classifier classifier = null;
		try {
			classifier = (Classifier) Class.forName("weka.classifiers.meta.AdaBoostM1").newInstance();
			classifier.setOptions(weka.core.Utils.splitOptions("weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I 10 -W weka.classifiers.trees.DecisionStump"));
			classifier.buildClassifier(instancesTrain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
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
			return "RefactorPoss:" + this.poss + "\tFeatureSet:" + this.ins.toString();
		}
	}

}
