package waterloo.Experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ForExp {

	String dataStarter = "@data", attrHeader = "@attribute";
	int classIndex = 0;
	String arffFileHeader = "";
	String lineBreak = "\n";
	String[] projectName = {"axis2-java", "eclipse.jdt.core", "elasticsearch", "jfreechart", "jruby", "lucene"};
	String[] features = {"noCoChangeFeature", "noCodeFeature", "noDiffFeature", "noHistoryFeature", "noLocationFeature"};
	String[] models = {"AdaBoost", "RandomForest", "C4.5", "SMO", "NaiveBayes"};
	static String basePath ;
	static String featureBasePath;
	static String outputBasePath;
	String crec = "crecFeature";
	String wangwei = "wangwei";
	String suffix = ".arff";
	
	public static void main(String[] args) throws Exception {
		System.out.println("start");
		ForExp t = new ForExp();
		if (args.length != 1) {
			System.out.println("Argument Wrong!");
		}
		else {
			basePath = args[0];
			featureBasePath = basePath + "features/";
			outputBasePath = basePath + "Results/";
			File file = new File(outputBasePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String modelOutputPath = outputBasePath + "DifferentModels/";
			file = new File(modelOutputPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			t.testModel(modelOutputPath);
			String featureOutputPath = outputBasePath + "DifferentFeatures/";
			file = new File(featureOutputPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			t.testFeature(featureOutputPath);
			String wwOutputPath = outputBasePath + "CompareWithWangWei/";
			file = new File(wwOutputPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			t.testWangWei(wwOutputPath);
		}
		System.out.println("end");
	}
	
	public void testWangWei(String outputBasePath) throws Exception {
		for (int j = 0; j < 4; j++) {
			String outputPath = outputBasePath;
			if (j == 0 || j == 1) {
				if (j == 0) {
					outputPath += "crecInAdaBoost-CrossProjects.txt";
				}
				else {
					outputPath += "crecInC4.5-CrossProjects.txt";
				}
				BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
				for (int i = 0; i < projectName.length; i++) {
					String trainName = featureBasePath + "train/no-" + projectName[i] + "-" + crec + suffix;
					String testName = featureBasePath + "test/" + projectName[i] + "/" + crec + suffix;
					System.out.println(trainName + " " + testName);
					w.write("*******************************" + "\n");
					w.write("Train:" + trainName + "\n");
					w.write("Test :" + testName + "\n");
					DataSource atf;
					File trainArffFile = new File(trainName);
					File testArffFile = new File(testName);
					this.classIndex = 0;
					this.initArffHeaderAndClassIndex(trainName);
					try {
						atf = new DataSource(trainArffFile.getAbsolutePath());
						Instances instancesTrain = atf.getDataSet();
						instancesTrain.setClassIndex(classIndex);
						atf = new DataSource(testArffFile.getAbsolutePath());
						Instances instancesTest = atf.getDataSet();
						instancesTest.setClassIndex(classIndex);
						if (j == 0) {
							Classifier adaboost = buildAdaBoostML(instancesTrain);
							evaluationWithTestset(adaboost, instancesTrain, instancesTest, w);
						}
						if (j == 1) {
							Classifier decisionTree = buildDecisionTree(instancesTrain);
							evaluationWithTestset(decisionTree, instancesTrain, instancesTest, w);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("ClassIndex:" + this.classIndex);
				}
				w.close();
			}
			else {
				if (j == 2) {
					outputPath += "wangweiInAdaBoost-CrossProjects.txt";
				}
				else {
					outputPath += "wangweiInC4.5-CrossProjects.txt";
				}
				BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
				for (int i = 0; i < projectName.length; i++) {
					String trainName = featureBasePath + "train/no-" + projectName[i] + "-" + wangwei + suffix;
					String testName = featureBasePath + "test/" + projectName[i] + "/" + wangwei + suffix;
					System.out.println(trainName + " " + testName);
					w.write("*******************************" + "\n");
					w.write("Train:" + trainName + "\n");
					w.write("Test :" + testName + "\n");
					DataSource atf;
					File trainArffFile = new File(trainName);
					File testArffFile = new File(testName);
					this.classIndex = 0;
					this.initArffHeaderAndClassIndex(trainName);
					try {
						atf = new DataSource(trainArffFile.getAbsolutePath());
						Instances instancesTrain = atf.getDataSet();
						instancesTrain.setClassIndex(classIndex);
						atf = new DataSource(testArffFile.getAbsolutePath());
						Instances instancesTest = atf.getDataSet();
						instancesTest.setClassIndex(classIndex);
						if (j == 2) {
							Classifier adaboost = buildAdaBoostML(instancesTrain);
							evaluationWithTestset(adaboost, instancesTrain, instancesTest, w);
						}
						if (j == 3) {
							Classifier decisionTree = buildDecisionTree(instancesTrain);
							evaluationWithTestset(decisionTree, instancesTrain, instancesTest, w);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("ClassIndex:" + this.classIndex);
				}
				w.close();
			}
			
		}
		
		/*for (int j = 0; j < 4; j++) {
			String outputPath = outputBasePath;
			if (j == 0 || j == 1) {
				if (j == 0) {
					outputPath += "crecInAdaBoost-WithinProjects-";
				}
				else {
					outputPath += "crecInC4.5-WithinProjects-";
				}
				
				String[] trainName = new String[projectName.length];
				String[] testName = new String[projectName.length];
				for (int i = 0; i < projectName.length; i++) {
					if (j == 0) {
						outputPath = outputBasePath + "crecInAdaBoost-WithinProjects-";
					}
					else {
						outputPath = outputBasePath + "crecInC4.5-WithinProjects-";
					}
					outputPath += projectName[i] +  ".txt";
					BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
					for (int cnt = 1; cnt <= 10; cnt ++) {
						trainName[i] = featureBasePath + "testWithinProjects/" + projectName[i] + "/train/trainInTen_" + cnt + ".arff";
						testName[i] = featureBasePath + "testWithinProjects/" + projectName[i] + "/test/testInTen_" + cnt + ".arff";
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
							if (j == 0) {
								Classifier adaboost = buildAdaBoostML(instancesTrain);
								evaluationWithTestset(adaboost, instancesTrain, instancesTest, w);
							}
							if (j == 1) {
								Classifier decisionTree = buildDecisionTree(instancesTrain);
								evaluationWithTestset(decisionTree, instancesTrain, instancesTest, w);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.out.println("ClassIndex:" + this.classIndex);
					}
					w.close();
				}
			}
			else {
				if (j == 2) {
					outputPath += "wangweiInAdaBoost-WithinProjects-";
				}
				else {
					outputPath += "wangweiInC4.5-WithinProjects-";
				}
				String[] trainName = new String[projectName.length];
				String[] testName = new String[projectName.length];
				for (int i = 0; i < projectName.length; i++) {
					if (j == 2) {
						outputPath = outputBasePath + "wangweiInAdaBoost-WithinProjects-";
					}
					else {
						outputPath = outputBasePath + "wangweiInC4.5-WithinProjects-";
					}
					outputPath += projectName[i] +  ".txt";
					BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
					for (int cnt = 1; cnt <= 10; cnt ++) {
						trainName[i] = featureBasePath + "testWithinProjects/" + projectName[i] + "/train/" + wangwei + "TrainInTen_" + cnt + ".arff";
						testName[i] = featureBasePath + "testWithinProjects/" + projectName[i] + "/test/" + wangwei + "TestInTen_" + cnt + ".arff";
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
							if (j == 2) {
								Classifier adaboost = buildAdaBoostML(instancesTrain);
								evaluationWithTestset(adaboost, instancesTrain, instancesTest, w);
							}
							if (j == 3) {
								Classifier decisionTree = buildDecisionTree(instancesTrain);
								evaluationWithTestset(decisionTree, instancesTrain, instancesTest, w);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.out.println("ClassIndex:" + this.classIndex);
					}
					w.close();
				}
			}
		}*/
	}
	
	public void testFeature(String outputBasePath) throws Exception {
		for (int j = 0; j < 5; j++) {
			String outputPath = outputBasePath + features[j] + "-CrossProjects.txt";
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
			for (int i = 0; i < projectName.length; i++) {
				String trainName = featureBasePath + "train/no-" + projectName[i] + "-" + features[j] + suffix;
				String testName = featureBasePath + "test/" + projectName[i] + "/" + features[j] + suffix;
				System.out.println(trainName + " " + testName);
				w.write("*******************************" + "\n");
				w.write("Train:" + trainName + "\n");
				w.write("Test :" + testName + "\n");
				DataSource atf;
				File trainArffFile = new File(trainName);
				File testArffFile = new File(testName);
				this.classIndex = 0;
				this.initArffHeaderAndClassIndex(trainName);
				try {
					atf = new DataSource(trainArffFile.getAbsolutePath());
					Instances instancesTrain = atf.getDataSet();
					instancesTrain.setClassIndex(classIndex);
					atf = new DataSource(testArffFile.getAbsolutePath());
					Instances instancesTest = atf.getDataSet();
					instancesTest.setClassIndex(classIndex);
					Classifier adaboost = buildAdaBoostML(instancesTrain);
					evaluationWithTestset(adaboost, instancesTrain, instancesTest, w);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("ClassIndex:" + this.classIndex);
			}
			w.close();
		}
		/*features[0] = "noCoChangeFeature";
		for (int j = 0; j < 5; j++) {
			String[] trainName = new String[projectName.length];
			String[] testName = new String[projectName.length];
			for (int i = 0; i < projectName.length; i++) {
				String outputPath = outputBasePath + features[j] + "-WithinProjects-" + projectName[i] +  ".txt";
				BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
				for (int cnt = 1; cnt <= 10; cnt ++) {
					trainName[i] = featureBasePath + "testWithinProjects/" + projectName[i] + "/train/" + features[j] + "TrainInTen_" + cnt + ".arff";
					testName[i] = featureBasePath + "testWithinProjects/" + projectName[i] + "/test/" + features[j] + "TestInTen_" + cnt + ".arff";
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
						Classifier adaboost = buildAdaBoostML(instancesTrain);
						evaluationWithTestset(adaboost, instancesTrain, instancesTest, w);

					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("ClassIndex:" + this.classIndex);
				}
				w.close();
			}
		}
		*/
	}
	
	public void testModel(String outputBasePath) throws Exception {
		for (int j = 0; j < 5; j++) {
			String outputPath = outputBasePath + models[j] + "-CrossProjects.txt";
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
			for (int i = 0; i < projectName.length; i++) {
				String trainName = featureBasePath + "train/no-" + projectName[i] + "-" + crec + suffix;
				String testName = featureBasePath + "test/" + projectName[i] + "/" + crec + suffix;
				System.out.println(trainName + " " + testName);
				w.write("*******************************" + "\n");
				w.write("Train:" + trainName + "\n");
				w.write("Test :" + testName + "\n");
				DataSource atf;
				File trainArffFile = new File(trainName);
				File testArffFile = new File(testName);
				this.classIndex = 0;
				this.initArffHeaderAndClassIndex(trainName);
				try {
					atf = new DataSource(trainArffFile.getAbsolutePath());
					Instances instancesTrain = atf.getDataSet();
					instancesTrain.setClassIndex(classIndex);
					atf = new DataSource(testArffFile.getAbsolutePath());
					Instances instancesTest = atf.getDataSet();
					instancesTest.setClassIndex(classIndex);
					if (j == 0) {
						Classifier adaboost = buildAdaBoostML(instancesTrain);
						evaluationWithTestset(adaboost, instancesTrain, instancesTest, w);
					}
					if (j == 1) {
						Classifier randomForest = buildRandomForest(instancesTrain);
						evaluationWithTestset(randomForest, instancesTrain, instancesTest, w);
					}
					if (j == 2) {
						Classifier decisionTree = buildDecisionTree(instancesTrain);
						evaluationWithTestset(decisionTree, instancesTrain, instancesTest, w);
					}
					if (j == 3) {
						Classifier svm = buildSVM(instancesTrain);
						evaluationWithTestset(svm, instancesTrain, instancesTest, w);
					}
					if (j == 4) {
						Classifier naiveBayes = buildNaiveBayes(instancesTrain);
						evaluationWithTestset(naiveBayes, instancesTrain, instancesTest, w);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("ClassIndex:" + this.classIndex);
			}
			w.close();
		}
		
		/*for (int j = 0; j < 5; j++) {
			String[] trainName = new String[projectName.length];
			String[] testName = new String[projectName.length];
			for (int i = 0; i < projectName.length; i++) {
				String outputPath = outputBasePath + models[j] + "-WithinProjects-" + projectName[i] +  ".txt";
				BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
				for (int cnt = 1; cnt <= 10; cnt ++) {
					trainName[i] = featureBasePath + "testWithinProjects/" + projectName[i] + "/train/trainInTen_" + cnt + ".arff";
					testName[i] = featureBasePath + "testWithinProjects/" + projectName[i] + "/test/testInTen_" + cnt + ".arff";
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
						if (j == 0) {
							Classifier adaboost = buildAdaBoostML(instancesTrain);
							evaluationWithTestset(adaboost, instancesTrain, instancesTest, w);
						}
						if (j == 1) {
							Classifier randomForest = buildRandomForest(instancesTrain);
							evaluationWithTestset(randomForest, instancesTrain, instancesTest, w);
						}
						if (j == 2) {
							Classifier decisionTree = buildDecisionTree(instancesTrain);
							evaluationWithTestset(decisionTree, instancesTrain, instancesTest, w);
						}
						if (j == 3) {
							Classifier svm = buildSVM(instancesTrain);
							evaluationWithTestset(svm, instancesTrain, instancesTest, w);
						}
						if (j == 4) {
							Classifier naiveBayes = buildNaiveBayes(instancesTrain);
							evaluationWithTestset(naiveBayes, instancesTrain, instancesTest, w);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("ClassIndex:" + this.classIndex);
				}
				w.close();
			}
		}
		*/
		
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
		} catch (Exception e) {
			e.printStackTrace();
		}
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
}