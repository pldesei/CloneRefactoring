package edu.pku.sei.codeclone.predictor;

import japa.parser.JavaParser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import edu.pku.sei.codeclone.predictor.MethodCallAnalyzer.MethodInvocationVisitor;
import edu.pku.sei.codeclone.predictor.MethodCallAnalyzer.ReadFile;
import edu.pku.sei.codeclone.predictor.MethodCallAnalyzer.SrcCollector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.TimeZone;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

class MyRefactoredClass {
	
	MyCloneClass pred;
	MyCloneClass next;
	String nextVersionRepoID;
	
	public MyRefactoredClass(MyCloneClass c1, MyCloneClass c2, String nextVersionRepoID) {
		this.pred = c1;
		this.next = c2;
		this.nextVersionRepoID = nextVersionRepoID;
	}
	
	public String toString() {
		String ret = "Instance #\n";
		ret += pred.toString();
		if (next != null)
			ret += next.toString();
		return ret+"\n";
	}
	
}

class MapUtil {
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue (Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}

class Node {
	MyCloneClass me;
	Node fa;
	Vector<Node> chs;
	
	public Node(MyCloneClass me, Node fa) {
		this.me = me;
		this.fa = fa;
		chs = new Vector<Node>();
	}
	
	public void addChild(Node ch) {
		chs.add(ch);
	}
}

public class ForGZTest {
	
	public static String basePath = "/home/sonia/NewExperiment/";
	public static String projectName = "jfreechart";
	public static String repoPath = basePath + projectName + "Filter/";
	
	public static String outputBasePath = basePath + "results/" + projectName + "/";
	public static String versionFolderPath = basePath + "versions/";
	public static String versionBasePath = versionFolderPath + projectName ;
	public static String versionPath = versionBasePath + "1-5000.txt";
 
	public static String refactoredOutputPath = outputBasePath + "refactored_" + projectName + "1-5000.txt";
	public static String refactoredOutputReadablePath = outputBasePath + "refactored_" + projectName + "_readable1-5000.txt";
	
	public static String unrefactoredOutputPath = outputBasePath + "unrefactored_" + projectName + ".txt";
	public static String unrefactoredReadableOutputPath = outputBasePath + "unrefactored_" + projectName + "_readable.txt";
	
	public static String printerPath = basePath + "outs/" + projectName + ".out";
	public static PrintStream printer = null;
	public static double simiThreshold = 0.4;
	
	public HashMap<String, HashMap<String, HashMap<String, Vector<MethodDeclaration>>>> versions = new HashMap<String, HashMap<String, HashMap<String, Vector<MethodDeclaration>>>>();
	public HashMap<String, HashSet<String>> srcPaths = new HashMap<String, HashSet<String>>();
	
	public List<MyVersion> v = new Vector<MyVersion>();
	
	public static void main(String args[]) throws Exception{
        if (args.length != 2)
        	System.out.println("Arguments Wrong!");
        else {
        	basePath = args[0];
        	projectName = args[1];
        	repoPath = basePath + "tmp/" + projectName + "Filter/";
        	outputBasePath = basePath + "tmp/results/" + projectName + "/";
        	File file = new File(outputBasePath);
        	if (!file.exists())
        		file.mkdirs();
        	versionFolderPath = basePath + "tmp/versions/";
        	file = new File(versionFolderPath);
        	if (!file.exists())
        		file.mkdirs();
        	versionBasePath = versionFolderPath + projectName ;
        	versionPath = versionBasePath + "1-5000.txt";
         
        	refactoredOutputPath = outputBasePath + "refactored_" + projectName + "1-5000.txt";
        	refactoredOutputReadablePath = outputBasePath + "refactored_" + projectName + "_readable1-5000.txt";
        	
        	unrefactoredOutputPath = outputBasePath + "unrefactored_" + projectName + ".txt";
        	unrefactoredReadableOutputPath = outputBasePath + "unrefactored_" + projectName + "_readable.txt";
        	
        	file = new File(basePath + "tmp/outs/");
        	if (!file.exists())
        		file.mkdirs();
        	printerPath = basePath + "tmp/outs/" + projectName + ".out";
        	printer = new PrintStream(new File(printerPath));
    		ForGZTest test = new ForGZTest();
    		test.filterRefactoredList(test.buildGenealogy(repoPath, repoPath, 1, 5000));
        }
	}
	
	private String outputTime() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss a Z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		return dateFormat.format(date);
	}
	
    public Vector<MyRefactoredClass> buildGenealogy(String srcPath, String resPath, int start, int end) {
    	System.out.println("Start time:" + outputTime());
        List<MyVersion> versions = MyVersionList.load(srcPath, resPath, start, end);
        
        try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(versionBasePath + start + "-" + end + ".txt")));
			oos.writeObject(versions);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("Finish link:" + outputTime());
        //return null;
        /*
    	List<MyVersion> versions = new Vector<MyVersion>();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(versionPath)));
			versions = (List<MyVersion>)ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(versions.size());
		*/
		System.out.println("Versions size is:" + versions.size());
		//System.out.println("After Reading Object:" + outputTime());
		
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
        			HashMap<MyFragment, Integer> myset = new HashMap<MyFragment, Integer>();
    				for (MyFragment frag : nowClass.fragments) {
    					myset.put(frag.predecessor, frag.endLine - frag.startLine);
    				}
    				int cnt = 0;
    				for (MyFragment frag : predCloneClass.fragments) 
    					if (!myset.containsKey(frag)) {
    						cnt ++;
    					}
    					else {
    						if ((frag.endLine - frag.startLine) > myset.get(frag)) {
    							cnt ++;
    						}
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
        
        System.out.println("After Getting Geogeology:" + outputTime());
        System.out.println("Size:" + refactoredList.size());
		return refactoredList;
    }
	
	private void filterRefactoredList(Vector<MyRefactoredClass> refactoredList) throws Exception {
		Vector<RefactoredInstance> refactoredInstanceList = new Vector<RefactoredInstance>();
		int number = 0;
		for (MyRefactoredClass refactoredClass: refactoredList) {
			//System.out.println("One class:" + outputTime());
			number ++;
			if (number % 100 == 0)
				System.out.println(number + ":" + refactoredList.size() + ":" + outputTime());
			MyCloneClass pred = refactoredClass.pred;
			MyCloneClass next = refactoredClass.next;
			Vector<MyFragment> frags = new Vector<MyFragment>();
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
					if (pred.fragments.contains(frag.predecessor)) {
						if ((frag.endLine - frag.startLine) < (frag.predecessor.endLine - frag.predecessor.startLine)) {
							if (!frags.contains(frag.predecessor))
								frags.addElement(frag.predecessor);
						}
					}
				}
				for (MyFragment frag : pred.fragments) 
					if (!myset.contains(frag)) {
						if (!frags.contains(frag))
							frags.addElement(frag);
					}
			}
			
			//HashMap<String, HashMap<MethodInvocation, MyFragment>> methodInvokeByFragments = new HashMap<String, HashMap<MethodInvocation, MyFragment>>();
			HashMap<String, Vector<MyFragment>> methodInvokeByFragments = new HashMap<String, Vector<MyFragment>>();
			HashMap<String, ITypeBinding[]> paraTypesByFragments = new HashMap<String, ITypeBinding[]>();
			for (MyFragment frag : frags) {
				
				String predPath = frag.srcPath;
				String nextPath = predPath.replace("/" + frag.versionRepoID, "/" + refactoredClass.nextVersionRepoID);
				//printer.println(predPath + "\n" + nextPath);
				String nextProjectPath = pred.versionBelongs.basePath.replace("/" + pred.versionBelongs.versionRepoID, "/" + refactoredClass.nextVersionRepoID);
				//printer.println("ProjectPath:" + nextProjectPath);

				HashSet<String> srcPathSet;
				SrcCollector srcCollector = new SrcCollector();
				if (srcPaths.containsKey(nextProjectPath)) {
					srcPathSet = srcPaths.get(nextProjectPath);
				}
				else {
					srcCollector.analyzeSrcPaths(nextProjectPath);
					srcPathSet = srcCollector.getSrcPathSet();
					srcPaths.put(nextProjectPath, srcPathSet);
				}
				
				Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(frag, predPath, nextPath, srcPathSet);
				if (methodInvokeList == null) continue;

				for (MethodInvocation methodInvo : methodInvokeList) {
					int lineNum = ((CompilationUnit) methodInvo.getRoot()).getLineNumber(methodInvo.getStartPosition());
					printer.println("%%%%%%%%%%");
					printer.println("AddedMethodInvo Line:" + lineNum + " InvoName:" + methodInvo);
					// resolve(get receiverFilePath and MethodSignature)
					IMethodBinding mBinding = methodInvo.resolveMethodBinding();
					// compare path with commonMethod
					String preferedSrcPath = srcCollector.filePathToSrcPath(new File(nextPath));
					String invokedFilePath = srcCollector.getFilePath(preferedSrcPath, mBinding, printer);
					if (invokedFilePath == null)
						continue;
					printer.println("InvokedFilePath:" + invokedFilePath);
					if(mBinding!=null)
						printer.println("InvokedMethodSig:" + mBinding.getMethodDeclaration().toString());
					else
						continue;
					String methodName = mBinding.getMethodDeclaration().getName();
					String para = mBinding.getMethodDeclaration().toString();
					String key = invokedFilePath + "::" + methodName + "::" + para;
					//System.out.println("---------");
					ITypeBinding[] ooo = mBinding.getMethodDeclaration().getParameterTypes();
					/*for (int j = 0; j < ooo.length; j++) {
						System.out.println(ooo[j].getName());
					}*/
					//System.out.println("---------");
					if (methodInvokeByFragments.get(key) != null) {
						Vector<MyFragment> list = methodInvokeByFragments.get(key);
						if (!list.contains(frag))
							list.add(frag);
						methodInvokeByFragments.put(key, list);
					}
					else {
						Vector<MyFragment> list = new Vector<MyFragment>();
						list.add(frag);
						methodInvokeByFragments.put(key, list);
						paraTypesByFragments.put(key, ooo);
					}
					// compare1: filePath containing invokedMethod
					// compare2: the declation of invokedMethod
					// IMethodBinding.getMethodDeclaration().toString()
					// e.g.,public String getStr(int i)
				} // end of addedMethodInvos
			}
			
			if (methodInvokeByFragments.size() > 0) {
			} else
				continue;
			
			HashMap<String, Vector<MyFragment>> tmp = new HashMap<String, Vector<MyFragment>>();
			for (String name : methodInvokeByFragments.keySet()) {
				if (methodInvokeByFragments.get(name).size() >= 2) 
					tmp.put(name, methodInvokeByFragments.get(name));
			}
			methodInvokeByFragments = tmp;

			if (methodInvokeByFragments.size() > 0) {
			} else 
				continue;
			

			if (!versions.containsKey(refactoredClass.nextVersionRepoID)) {
				String version = refactoredClass.nextVersionRepoID;
				HashMap<String, HashMap<String, Vector<MethodDeclaration>>> t = new HashMap<String, HashMap<String, Vector<MethodDeclaration>>>();
				versions.put(version, t);
			}
			if (!versions.containsKey(pred.versionBelongs.versionRepoID)) {
				String version = pred.versionBelongs.versionRepoID;
				HashMap<String, HashMap<String, Vector<MethodDeclaration>>> t = new HashMap<String, HashMap<String, Vector<MethodDeclaration>>>();
				versions.put(version, t);
			}
			HashMap<String, HashMap<String, Vector<MethodDeclaration>>> add = versions.get(refactoredClass.nextVersionRepoID);
			MyFragment common = null;
			for (String name : methodInvokeByFragments.keySet()) {
				ITypeBinding[] types = paraTypesByFragments.get(name);
				Vector<MyFragment> frag = methodInvokeByFragments.get(name);
				String[] opt = name.split("::");
				String filePath = opt[0];
				String methodName = opt[1];
				//String para = opt[2];
				if (!add.containsKey(filePath)) {
					getMethodDeclaration(filePath, refactoredClass.nextVersionRepoID);
				}
				HashMap<String, Vector<MethodDeclaration>> methods = add.get(filePath);
				if (methods == null)
					continue;
				if (!methods.containsKey(methodName)) 
					continue;
				Vector<MethodDeclaration> method = methods.get(methodName);
				for (MethodDeclaration wait : method) {
					//System.out.println("Name:" + wait.getName().toString());
					//System.out.println("All:" + wait.toString());
					List<SingleVariableDeclaration> paras = wait.parameters();
					if (!check(types, paras)) {
						continue;
					}
						int startline = 0;
						int endline = 0;
						if (wait.getBody() != null) {
							startline = ((CompilationUnit) wait.getRoot()).getLineNumber(wait.getBody().getStartPosition());
							endline = ((CompilationUnit) wait.getRoot()).getLineNumber(wait.getBody().getStartPosition() + wait.getBody().getLength());
						}
						else {
							startline = ((CompilationUnit) wait.getRoot()).getLineNumber(wait.getStartPosition());
							endline = ((CompilationUnit) wait.getRoot()).getLineNumber(wait.getStartPosition() + wait.getLength());
						}
						common = new MyFragment(startline, endline, -1, -1, filePath, Integer.parseInt(refactoredClass.nextVersionRepoID), refactoredClass.nextVersionRepoID);
						Vector<Double> ret = calcSimilarity(frag, common);
						
						Vector<MyFragment> fragtmp = new Vector<MyFragment> ();
						Vector<Double> rettmp = new Vector<Double>();
						for (int i = 0; i < ret.size(); i++) {
							if (ret.elementAt(i) > 0.1) {
								fragtmp.addElement(frag.get(i));
								rettmp.addElement(ret.elementAt(i));
							}
						}
						
						Vector<MyFragment> fragtmptmp = new Vector<MyFragment> ();
						for (int i = 0; i < ret.size(); i++) {
							if (ret.elementAt(i) >= simiThreshold) {
								fragtmptmp.addElement(frag.get(i));
							}
						}
						
						if (fragtmptmp.size() >= 2) {
							startline = ((CompilationUnit) wait.getRoot()).getLineNumber(wait.getStartPosition());
							endline = ((CompilationUnit) wait.getRoot()).getLineNumber(wait.getStartPosition() + wait.getLength());
							common = new MyFragment(startline, endline, -1, -1, filePath, Integer.parseInt(refactoredClass.nextVersionRepoID), refactoredClass.nextVersionRepoID);
							RefactoredInstance t = new RefactoredInstance(fragtmp, common, rettmp);
							refactoredInstanceList.addElement(t);
							
							String path = outputBasePath + "refactor_" + (refactoredInstanceList.size()) + ".txt";
							String readablePath = outputBasePath + "refactor_readable_" + (refactoredInstanceList.size()) + ".txt";
							try {
								RefactoredInstance ins = (RefactoredInstance)t.clone();
									for (MyFragment f : ins.frags) {
										f.fatherCloneClass = null;
										f.predecessor = null;
									}
									ins.commonMethod.predecessor = null;
									ins.commonMethod.fatherCloneClass = null;
								
								ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)));
								oos.writeObject(ins);
								oos.close();
								
								FileOutputStream fos = new FileOutputStream(new File(readablePath));
								fos.write(t.toString().getBytes());
								fos.close();
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							System.out.println("Get " + refactoredInstanceList.size() + " refactor instance:" + outputTime());
							
						}
				}
			}
		}
		System.out.println("printing");
		PrintResult(refactoredInstanceList);
		System.out.println("resultCnt = " + refactoredInstanceList.size());
	}

	private boolean check(ITypeBinding[] types, List<SingleVariableDeclaration> paras) {
		if (paras == null || paras.size() == 0) {
			if (types == null || types.length == 0)
				return true;
			else 
				return false;
		}
		else{
			if (types == null || paras.size() != types.length)
				return false;
			else {
				for (int i = 0; i < paras.size(); i++) {
					if (!paras.get(i).getType().toString().equals(types[i].getName())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private void getMethodDeclaration(String filePath, String version) {	
		File file = new File(filePath);
		judge(file, version);
	}
	
	public void judge(File tmpFile, String version) {
		if (!tmpFile.exists())
			return;
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
				HashMap<String, HashMap<String, Vector<MethodDeclaration>>> tmp = versions.get(version);
				MethodInvocationVisitor v = null;
				CompilationUnit cu = null;

				ASTParser parser = ASTParser.newParser(AST.JLS8);
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				Hashtable<String, String> options = JavaCore.getOptions();
				options.put(JavaCore.COMPILER_SOURCE, "1.8");

				parser.setSource(ReadFile.fileToCharArray(tmpFile));
				cu = (CompilationUnit) parser.createAST(null);
				v = new MethodInvocationVisitor();
				cu.accept(v);
				//printer.println("end of visitor");
				tmp.put(tmpFile.getAbsolutePath(), v.methodDeclareMap);
				versions.put(version, tmp);
			}
		}
	}

	private void PrintResult(Vector<RefactoredInstance> t) {
		try {
			Vector<RefactoredInstance> tmp = (Vector<RefactoredInstance>)t.clone();
			for (RefactoredInstance ins : tmp) {
				for (MyFragment frag : ins.frags) {
					frag.fatherCloneClass = null;
					frag.predecessor = null;
				}
				ins.commonMethod.predecessor = null;
				ins.commonMethod.fatherCloneClass = null;
			}
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(refactoredOutputPath)));
			oos.writeObject(tmp);
			oos.close();
			
			FileOutputStream fos = new FileOutputStream(new File(refactoredOutputReadablePath));
			fos.write(t.toString().getBytes());
			fos.close();
			
			for (int cnt = 0; cnt < t.size(); cnt ++) {
				RefactoredInstance ins = t.get(cnt);
				String singleInstanceFolderPath = basePath + "Results/RefactorInstances/" + "refactor" + (cnt+1);
				File singleInstanceFolder = new File(singleInstanceFolderPath);
				if (!singleInstanceFolder.exists())
					singleInstanceFolder.mkdirs();
				Vector<MyFragment> frags = ins.frags;
				for (int i = 0; i < frags.size(); i++) {
					File fragFile = new File(singleInstanceFolderPath + "/cloneInstance" + (i + 1) + ".txt");
					PrintWriter pw = new PrintWriter(new FileWriter(fragFile));
					pw.println(frags.get(i).toString());
					pw.close();
				}
				File commonMethodFile = new File(singleInstanceFolderPath + "/commonMethod.txt");
				PrintWriter pw = new PrintWriter(new FileWriter(commonMethodFile));
				pw.println(ins.commonMethod.commonToString());
				pw.close();
				printOldAndNewFile(frags, singleInstanceFolderPath);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printOldAndNewFile(List<MyFragment> frags, String refactorInsFolderPath) {
		for (int i = 0; i < frags.size(); i++) {
			MyFragment frag = frags.get(i);
			String oldFilePath = frag.getFilePath();
			File oldCloneFromFile = new File(oldFilePath);

			File oldCloneToFile = new File(
					refactorInsFolderPath + "/" + "oldCloneFile" + (i + 1) + ".txt");
			copyFile(oldCloneFromFile, oldCloneToFile);

			String newFilePath = frag.getNextFilePath();
			File newCloneFromFile = new File(newFilePath);
			File newCloneToFile = new File(
					refactorInsFolderPath + "/" + "newCloneFile" + (i + 1) + ".txt");
			copyFile(newCloneFromFile, newCloneToFile);

		}
	}

	private void copyFile(File source, File dest) {
		try {
			if (dest.exists())
				dest.delete();
			Files.copy(source.toPath(), dest.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Vector<Double> calcSimilarity(Vector<MyFragment> frags, MyFragment common) {
		Vector<Double> ret = new Vector<Double>();
		GZTokenVisitor commonv = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(common.srcPath);
			japa.parser.ast.CompilationUnit cu = JavaParser.parse(in);
			commonv = new GZTokenVisitor();
			commonv.visit(cu, null);
			in.close();
		} catch (Exception ex) {
			return ret;
		}
		catch (Error err) {
			printer.println("Error happened: \n" + common.srcPath);
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
			GZTokenVisitor fragv = null;
			//FileInputStream in = null;
			try {
				in = new FileInputStream(frag.srcPath);
				japa.parser.ast.CompilationUnit cu = JavaParser.parse(in);
				fragv = new GZTokenVisitor();
				fragv.visit(cu, null);
				in.close();
			} catch (Exception e) {
				//e.printStackTrace();
				continue;
			}
			catch (Error err) {
				printer.println("Error happened: \n" + frag.srcPath);
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
	
	private Vector<MethodInvocation> detectAddedMethodInvoke(MyFragment frag, String predPath, String nextPath, HashSet<String> srcPathSet) throws Exception {
		int[] linemap = buildLineMap(nextPath, predPath);
		if (linemap == null) {
			printer.println("linemap is null");
			return null;
		}

		if (linemap.length <= frag.startLine - 1) {
			System.out.println("Wrong here. frag:\n" + frag);
			System.out.println("linemap:\n" + linemap.length);
			return new Vector<MethodInvocation>();
		}
		int nextStart, nextEnd;
		MethodInvocationVisitor predVisitor = null, nextVisitor = null;
		File file = null;
		CompilationUnit cu = null;

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, "1.8");

		file = new File(predPath);
		if (!file.exists())
			printer.println("predFile unExist");
		parser.setSource(ReadFile.fileToCharArray(file));
		cu = (CompilationUnit) parser.createAST(null);
		predVisitor = new MethodInvocationVisitor(printer);
		cu.accept(predVisitor);

		file = new File(nextPath);
		if (!file.exists())
			printer.println("nextFile unExist");
		parser.setSource(ReadFile.fileToCharArray(file));
		// for ResolveMethodBinding
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setUnitName(file.getName());

		if (srcPathSet.size() != 0) {
			parser.setEnvironment(null, srcPathSet.toArray(new String[srcPathSet.size()]), null, true);
		} else {
			parser.setEnvironment(null, null, null, true);
		}
		// end of resolveMethodBinding
		cu = (CompilationUnit) parser.createAST(null);
		nextVisitor = new MethodInvocationVisitor(printer);
		cu.accept(nextVisitor);

		HashSet<MethodDeclaration> predMethodDecSet = predVisitor.methodDeclareSet;
		int predstart = frag.startLine, predend = frag.endLine;
		printer.println("predFragLineRange:" + predstart + "-" + predend);
		for (MethodDeclaration methodDec : predMethodDecSet) {
			int methodStartLine = ((CompilationUnit) methodDec.getRoot()).getLineNumber(methodDec.getStartPosition());
			int methodEndLine = ((CompilationUnit) methodDec.getRoot())
					.getLineNumber(methodDec.getStartPosition() + methodDec.getLength());
			if (methodStartLine <= frag.startLine && methodEndLine >= frag.endLine) {
				predstart = methodStartLine;
				predend = methodEndLine;
				break;
			}
		}
		printer.println("predMethodLineRange:" + predstart + "-" + predend);
		nextStart = linemap[predstart - 1] + 1;
		if (linemap[predstart - 1] == -1) {
			for (int i = predstart - 1; i <= predend - 1; i++)
				if (linemap[i] != -1) {
					nextStart = linemap[i];
					break;
				}
		}
		nextEnd = linemap[predend - 1] + 1;
		if (linemap[predend - 1] == -1) {
			for (int i = predend - 1; i >= predstart - 1; i--)
				if (linemap[i] != -1) {
					nextEnd = linemap[i];
					break;
				}
		}
		if (nextStart <= 0 || nextEnd <= 0)
			return null;
		printer.println("nextLineRange:" + nextStart + "-" + nextEnd);

		Vector<String> predMethodInvoNames = new Vector<String>();
		for (int i = predstart; i <= predend; i++) {
			Vector<MethodInvocation> predMethodInvosThisLine = predVisitor.lineMethodInvoMap.get(i);
			if (predMethodInvosThisLine == null)
				continue;
			for (MethodInvocation tmp : predMethodInvosThisLine) {
				predMethodInvoNames.add(tmp.getName().toString());
			}
		}
		printer.println("predStrMethodInvoNames:");
		printer.println(predMethodInvoNames);

		Vector<MethodInvocation> addedMethodCalls = new Vector<MethodInvocation>();
		for (int i = nextStart; i <= nextEnd; i++) {
			Vector<MethodInvocation> nextMethodCallsThisLine = nextVisitor.lineMethodInvoMap.get(i);
			if (nextMethodCallsThisLine == null)
				continue;
			for (MethodInvocation tmp : nextMethodCallsThisLine) {
				if (!predMethodInvoNames.contains(tmp.getName().toString())) {
					addedMethodCalls.addElement(tmp);
				}
			}
		}
		return addedMethodCalls;
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
	
}
