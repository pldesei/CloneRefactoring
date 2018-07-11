package waterloo.Experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class TopKPrecision {
	String dataStarter = "@data", attrHeader = "@attribute";
	int classIndex = 0;
	String arffFileHeader = "";
	String lineBreak = "\r";
	int topK = 10;
	BufferedWriter w ;
	String refactorLabel = "refactored";

	public static void main(String[] args) throws Exception {
		System.out.println("start");
		TopKPrecision t = new TopKPrecision();
		t.work();
		System.out.println("end");
	}

	public void work() throws Exception {
		String[] projectName = {"lucene", "guava", "es", "emf", "eclipse", "ant"};
		String[] featureSet = 
			{"groupFeatures", "groupFeaturesClonediff", "groupFeaturesConchange", "groupFeaturesConchangeClonediff", 
			"groupFeaturesHis", "groupFeaturesHisClonediff", "groupFeaturesHisConchange", "groupFeaturesHisConchangeClonediff"};
		
		String[] trainName = {"ant+eclipse+emf+es+guava", "ant+eclipse+emf+es+lucene", 
				"ant+eclipse+emf+guava+lucene", "ant+eclipse+es+guava+lucene", 
				"ant+emf+es+guava+lucene", "eclipse+emf+es+guava+lucene"};
		String filename = ".arff";
		String folderPath = "/home/ubuntu/NoMonth-0.3/";
			
		
		w = new BufferedWriter(new FileWriter(new File("/home/ubuntu/result-adaboost-" + topK +".txt")));
		
		
		
		for (int j = 0; j < 8; j++) {
			w.write("*******************************" + "\n");
			for (int i = 0; i < 6; i++) {
				String trainFileName = folderPath + "train/" + trainName[i] + featureSet[j] + filename;
				String testFileName = folderPath + "test/" + projectName[i] + "/" + featureSet[j] + filename;
				//w.write("*******************************" + "\n");
				int[] topNCloneIndexs = new int[topK];
				for (int cloneIndex : topNCloneIndexs)
					cloneIndex = -1;
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
					//topNCloneIndexs=rankEvaluation(decisionTree, instancesTrain, instancesTest);

					//Classifier svm = buildSVM(instancesTrain);
					//rankEvaluation(svm, instancesTrain, instancesTest);
					
					//Classifier randomForest = buildRandomForest(instancesTrain);
					//rankEvaluation(randomForest, instancesTrain, instancesTest);
					
					Classifier adaboost = buildAdaBoostML(instancesTrain);
					rankEvaluation(adaboost, instancesTrain, instancesTest);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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
	
	private int[] rankEvaluation(Classifier classifier, Instances instancesTrain, Instances instancesTest) {
		int[] topNCloneIndexs = new int[topK];
		for (int cloneIndex : topNCloneIndexs)
			cloneIndex = -1;
		try {
			Evaluation eval = new Evaluation(instancesTrain);
			eval.evaluateModel(classifier, instancesTest);
			ArrayList<NewInstance> refactorPossListForTest = new ArrayList<NewInstance>();
			//w.write("numTestInstances:" + instancesTest.numInstances() + "\n");
			for (int i = 0; i < instancesTest.numInstances(); i++) {
				Instance ins = instancesTest.instance(i);
				double[] possDis = classifier.distributionForInstance(ins);
				// class is {refactored, unrefactored}, 0 represents refactored
				// clones
				NewInstance newIns = new NewInstance(instancesTest.instance(i), possDis[0],i);
				refactorPossListForTest.add(newIns);
			}
			Collections.sort(refactorPossListForTest, new Comparator<NewInstance>() {
				@Override
				public int compare(NewInstance arg0, NewInstance arg1) {
					return arg0.poss.compareTo(arg1.poss);
				}
			});
			int len = refactorPossListForTest.size();
			int topNCloneCnt=0;
			int cnt = 0;
			int rightcnt = 0;
			for (int i = len - 1; i > len - 1 - topK; i--) {
				//w.write(refactorPossListForTest.get(i) + "\n");
				topNCloneIndexs[topNCloneCnt]=refactorPossListForTest.get(i).cloneIndex;
				topNCloneCnt++;	
				String label = refactorPossListForTest.get(i).ins.stringValue(classIndex);
				//System.out.println(label);
				if (refactorPossListForTest.get(i).poss > 0.5) {
					cnt ++;
					if (label.equals(refactorLabel)) {
						rightcnt ++;
					}
				}
				
				
			}
			w.write("" + ((double)rightcnt)/cnt+"\n");
			//w.write("AvePoss:" + ((double)rightcnt)/cnt + "\tRefactored Instance:" + cnt +" PredictRight: " + rightcnt +"\n");
			//w.write("***************" + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topNCloneIndexs;
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
