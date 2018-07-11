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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
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

public class NewForGZTest {
	
	public static String basePath = "/home/sonia/NewExperiment/";
	public static String projectName = "eclipse.jdt.core";
	public static String repoPath = basePath + projectName + "Filter/";
	
	public static String outputBasePath = basePath + "results/test/" + projectName + "/";
	public static String versionFolderPath = basePath + "versions/";
	public static String versionBasePath = versionFolderPath + projectName ;
	public static String versionPath = versionBasePath + "2400-3000.txt";
 
	public static String refactoredOutputPath = outputBasePath + "refactored_" + projectName + "2400-3000.txt";
	public static String refactoredOutputReadablePath = outputBasePath + "refactored_" + projectName + "_readable2400-3000.txt";
	
	public static String unrefactoredOutputPath = outputBasePath + "unrefactored_" + projectName + ".txt";
	public static String unrefactoredReadableOutputPath = outputBasePath + "unrefactored_" + projectName + "_readable.txt";
	
	public static String printerPath = basePath + "outs/" + projectName + ".out";
	public static PrintStream printer = null;
	public static double simiThreshold = 0.1;
	
	public HashMap<String, HashMap<String, HashMap<String, Vector<MethodDeclaration>>>> versions = new HashMap<String, HashMap<String, HashMap<String, Vector<MethodDeclaration>>>>();
	public HashMap<String, HashSet<String>> srcPaths = new HashMap<String, HashSet<String>>();
	public HashMap<String, HashSet<MethodDeclaration>> fileDeclares = new HashMap<String, HashSet<MethodDeclaration>>();

	public List<MyVersion> v = new Vector<MyVersion>();
	
	public static void main(String args[]) throws Exception{
        
		printer = new PrintStream(new File(printerPath));
		NewForGZTest test = new NewForGZTest();
		test.filterRefactoredList(test.buildGenealogy(repoPath, repoPath, 1, 1000));
	}
	
	private String outputTime() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss a Z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		return dateFormat.format(date);
	}
	
    public Vector<MyRefactoredClass> buildGenealogy(String srcPath, String resPath, int start, int end) {
    	
        /*List<MyVersion> versions = MyVersionList.load(srcPath, resPath, start, end);
        
        try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(versionBasePath + start + "_" + end + ".txt")));
			oos.writeObject(versions);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return null;
        */
        System.out.println("Start time:" + outputTime());
    	List<MyVersion> versions = new Vector<MyVersion>();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(versionPath)));
			versions = (List<MyVersion>)ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(versions.size());

		System.out.println(versions.size());
		System.out.println("After Reading Object:" + outputTime());
		
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
		int method1 = 0;
		int method2 = 0;
		int method3 = 0;
		int number = 0;
		for (MyRefactoredClass refactoredClass: refactoredList) {
			System.out.println("One class:" + outputTime());
			number ++;
			if (number % 100 == 0)
				System.out.println(number + ":" + refactoredList.size());
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
			
			HashMap<String, Vector<MyFragment>> methodInvokeByFragments = new HashMap<String, Vector<MyFragment>>();
			HashMap<String, ITypeBinding[]> paraTypesByFragments = new HashMap<String, ITypeBinding[]>();
			for (MyFragment frag : frags) {
				
				String predPath = frag.srcPath;
				String nextPath = predPath.replace("/" + frag.versionRepoID, "/" + refactoredClass.nextVersionRepoID);
				//printer.println(predPath + "\n" + nextPath);
				String nextProjectPath = pred.versionBelongs.basePath.replace("/" + pred.versionBelongs.versionRepoID, "/" + refactoredClass.nextVersionRepoID);
				//printer.println("ProjectPath:" + nextProjectPath);
				
				String thisProjectPath = pred.versionBelongs.basePath;
				HashSet<String> thissrcPathSet;
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
				
				if (srcPaths.containsKey(thisProjectPath)) {
					thissrcPathSet = srcPaths.get(thisProjectPath);
				}
				else {
					srcCollector.analyzeSrcPaths(thisProjectPath);
					thissrcPathSet = srcCollector.getSrcPathSet();
					srcPaths.put(thisProjectPath, thissrcPathSet);
				}
				
				
				Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(frag, predPath, nextPath, srcPathSet, thissrcPathSet);
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
					ITypeBinding[] ooo = mBinding.getMethodDeclaration().getParameterTypes();
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
			
			if (methodInvokeByFragments.size() > 0)
 				method1 ++;
			else
				continue;
			
			HashMap<String, Vector<MyFragment>> tmp = new HashMap<String, Vector<MyFragment>>();
			for (String name : methodInvokeByFragments.keySet()) {
				if (methodInvokeByFragments.get(name).size() >= 2) 
					tmp.put(name, methodInvokeByFragments.get(name));
			}
			methodInvokeByFragments = tmp;

			if (methodInvokeByFragments.size() > 0)
				method2 ++;
			else 
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
							if (ret.elementAt(i) > simiThreshold) {
								fragtmp.addElement(frag.get(i));
								rettmp.addElement(ret.elementAt(i));
							}
						}
						
						if (fragtmp.size() >= 2) {
							startline = ((CompilationUnit) wait.getRoot()).getLineNumber(wait.getStartPosition());
							endline = ((CompilationUnit) wait.getRoot()).getLineNumber(wait.getStartPosition() + wait.getLength());
							common = new MyFragment(startline, endline, -1, -1, filePath, Integer.parseInt(refactoredClass.nextVersionRepoID), refactoredClass.nextVersionRepoID);
							RefactoredInstance t = new RefactoredInstance(fragtmp, common, rettmp);
							refactoredInstanceList.addElement(t);
							
							String path = outputBasePath + "refactor_" + (refactoredInstanceList.size()+0) + ".txt";
							String readablePath = outputBasePath + "refactor_readable_" + (refactoredInstanceList.size()+0) + ".txt";
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
			
			if (common != null)
				method3 ++;
		}
		//refactoredInstanceList = filter(refactoredInstanceList);
		//printer.println("filtering");
		//refactoredInstanceList = filterCommonMethod(refactoredInstanceList);
		//refactoredInstanceList = getChangeLines(refactoredInstanceList);
		System.out.println("printing");
		PrintResult(refactoredInstanceList);
		System.out.println("method1 = " + method1);
		System.out.println("method2 = " + method2);
		System.out.println("method3 = " + method3);
		System.out.println("resultCnt = " + refactoredInstanceList.size());
	}

	private Vector<MethodInvocation> detectAddedMethodInvoke(MyFragment frag,
			String predPath, String nextPath, HashSet<String> srcPathSet,
			HashSet<String> thissrcPathSet) {
		
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
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setUnitName(file.getName());
		if (thissrcPathSet.size() != 0) {
			parser.setEnvironment(null, thissrcPathSet.toArray(new String[thissrcPathSet.size()]), null, true);
		} else {
			parser.setEnvironment(null, null, null, true);
		}
		cu = (CompilationUnit) parser.createAST(null);
		predVisitor = new MethodInvocationVisitor(printer);
		cu.accept(predVisitor);

		file = new File(nextPath);
		if (!file.exists()) {
			printer.println("nextFile unExist");
			return null;
		}		
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
		
		
		int predstart = frag.startLine, predend = frag.endLine;
		HashSet<MethodDeclaration> set = predVisitor.methodDeclareSet;
		String methodName = "";
		String className = "";
		List<SingleVariableDeclaration> paras = new Vector<SingleVariableDeclaration>();
		for (MethodDeclaration m : set) {
			int startline = ((CompilationUnit) m.getRoot()).getLineNumber(m.getStartPosition());
			int endline = ((CompilationUnit) m.getRoot()).getLineNumber(m.getStartPosition() + m.getLength());
			if (startline <= frag.startLine && endline >= frag.endLine) {
				methodName = m.getName().toString();
				IMethodBinding mBinding = m.resolveBinding();
				if (mBinding == null) {
					continue;
				}
				className = mBinding.getDeclaringClass().getQualifiedName();
				//System.out.println("QualifiedClassName:" + className);
				predstart = startline;
				predend = endline;
				paras = m.parameters();
				break;
			}
		}
		if (methodName.equals("")) {
			System.out.println("Something wrong");
			return null;
		}
	
		set = nextVisitor.methodDeclareSet;
		MethodDeclaration target = null;
		for (MethodDeclaration method : set) {
			String thisName = method.getName().toString();
			if (thisName.equals(methodName)) {
				IMethodBinding mBinding = method.resolveBinding();
				if (mBinding == null) {
					continue;
				}
				String qualifiedClassName = mBinding.getDeclaringClass().getQualifiedName();
				//System.out.println("ClassName:" + qualifiedClassName);
				if (qualifiedClassName == null || qualifiedClassName.equals("")) {
					continue;
				}
				if (qualifiedClassName.equals(className)) {
					List<SingleVariableDeclaration> thisparas = method.parameters();
					if (paras.size() != thisparas.size()) {
						continue;
					}
					boolean flag = true;
					for (int i = 0; i < paras.size(); i++) {
						String str1 = paras.get(i).getType().toString();
						String str2 = thisparas.get(i).getType().toString();
						if (!str1.equals(str2)) {
							flag = false;
							break;
						}
					}
					if (flag) {
						target = method;
						break;
					}
				}
			}
		}
		
		if (target == null) {
			return null;
		}

		printer.println("predMethodLineRange:" + predstart + "-" + predend);
		nextStart = ((CompilationUnit) target.getRoot()).getLineNumber(target.getStartPosition());
		nextEnd = ((CompilationUnit) target.getRoot()).getLineNumber(target.getStartPosition() + target.getLength());
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
			
		} catch (Exception e) {
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
