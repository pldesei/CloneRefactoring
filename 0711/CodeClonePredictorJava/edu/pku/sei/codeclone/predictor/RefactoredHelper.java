package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import edu.pku.sei.codeclone.predictor.MethodCallAnalyzer.MethodInvocationVisitor;
import edu.pku.sei.codeclone.predictor.MethodCallAnalyzer.ReadFile;
import edu.pku.sei.codeclone.predictor.MethodCallAnalyzer.SrcCollector;

public class RefactoredHelper {

	public String basePath = "/home/sonia/NewExperiment/";
	
	public String projectName = "jruby";
	public String refactorPath = "/home/sonia/NewExperiment/results/refactorInstances/" + projectName;
	public String infoPath = "/home/sonia/CloneRefactoring/NewExperiment/ManuCheck/afterTwiceFilter/" + projectName + "/";
	public String path1 = infoPath + "0.1/";
	public String path3 = infoPath + "0.3/";
	public String path4 = infoPath + "0.4/";
	public String path5 = infoPath + "0.5/";
	
	public String filterFilePath = infoPath + "result";
	
	public PrintStream printer = null;
	public String printerPath = basePath + "outs/" + projectName + ".out";

	
	public Vector<RefactoredInstance> inses = new Vector<RefactoredInstance>();
	public HashMap<String, HashSet<String>> srcPaths = new HashMap<String, HashSet<String>>();
	
	public HashMap<String, HashSet<MethodDeclaration>> fileDeclares = new HashMap<String, HashSet<MethodDeclaration>>();

	public static void main(String[] args) throws Exception {
		RefactoredHelper helper = new RefactoredHelper();
		helper.printer = new PrintStream(new File(helper.printerPath));
		//helper.filterSameInvocation();
		//helper.getFilterInfo();
		//helper.filterWrongLineMap();
		helper.countCloneGroups();
	}
	
	private String outputTime() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss a Z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		return dateFormat.format(date);
	}
	
	public void countCloneGroups() throws Exception{
		String[] projects = {"eclipse.jdt.core", "jfreechart", "jruby", "lucene"};
		String versionBasePath = "/home/sonia/NewExperiment/versions/";
		File file = new File(versionBasePath);
		File[] files = file.listFiles();
		for (int i = 0; i < projects.length; i++) {
			int cnt = 0;
			System.out.println(projects[i]);
			String projectName = projects[i];
			Vector<File> filePath = new Vector<File>();
			for (File tmp : files) {
				if (tmp.getName().contains(projectName)) {
					filePath.add(tmp);
				}
			}
			Collections.sort(filePath, new SortByVersion());
			System.out.println(filePath);
			System.out.println("Start time:" + outputTime());
	    	List<MyVersion> versions = new Vector<MyVersion>();
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.get(0)));
				versions = (List<MyVersion>)ois.readObject();
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(versions.size());
			for (MyVersion v : versions) {
				cnt += v.clones.size();
			}
			
			for (int j = 1; j < filePath.size(); j++) {
				try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.get(j)));
					versions = (List<MyVersion>)ois.readObject();
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(versions.size());
				for (int k = 1; k < versions.size(); k++) {
					cnt += versions.get(k).clones.size();
				}
			}
			System.out.println("All Clone Groups:" + cnt);
		}
	}
	
	
	
	public void filterWrongLineMap() throws Exception {
		inses.clear();
		File cloneFileFolder = new File(refactorPath);
		ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
		ArrayList<File> refactorCloneFileList = new ArrayList<File>();
		for (File cloneFile : cloneFileFolder.listFiles()) {
			String fileName = cloneFile.getName();
			if (fileName.contains("readable"))
				continue;
			if (fileName.contains("unrefactored"))
				unrefactorCloneFileList.add(cloneFile);
			else if (fileName.contains("refactored"))
				refactorCloneFileList.add(cloneFile);
		}
		Collections.sort(unrefactorCloneFileList, new SortByVersion());
		Collections.sort(refactorCloneFileList, new SortByVersion());
		System.out.println(refactorCloneFileList);
		System.out.println(unrefactorCloneFileList);
		Vector<RefactoredInstance> tmp = new Vector<RefactoredInstance>();
		for (File cloneFile : refactorCloneFileList) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
				tmp = (Vector<RefactoredInstance>) ois.readObject();
				ois.close();
			} catch (Exception e) {
				//throw e;
				e.printStackTrace();
			}
			System.out.println(tmp.size());
			for (RefactoredInstance ins : tmp) {
				inses.addElement(ins);
			}
		}
		System.out.println("All Instances:" + inses.size());

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
		
		System.out.println("Filter Number:" + unsatisfy.size());
		
		Vector<Integer> anotherunsatisfy = new Vector<Integer>();
		for (int cnt = 0; cnt < inses.size(); cnt++) {
			RefactoredInstance ins = inses.elementAt(cnt);
			if (unsatisfy.contains(cnt)) {
				continue;
			}

			MyFragment method = ins.commonMethod;
			HashSet<MethodDeclaration> set = null;
			if (fileDeclares.containsKey(method.srcPath)) {
				set = fileDeclares.get(method.srcPath);
			}
			else {
				set = getDeclare(method.srcPath);
			}
			
			String methodName = "";
			for (MethodDeclaration m : set) {
				int startline = ((CompilationUnit) m.getRoot()).getLineNumber(m.getStartPosition());
				int endline = ((CompilationUnit) m.getRoot()).getLineNumber(m.getStartPosition() + m.getLength());
				if (startline == method.startLine && endline == method.endLine) {
					methodName = m.getName().toString();
					break;
				}
			}
			if (methodName.equals("")) {
				System.out.println("Something wrong");
				anotherunsatisfy.add(cnt);
				continue;
			}
			//System.out.println(methodName);
			
			Vector<MyFragment> frags = ins.frags;
			MethodInvocation[] added = new MethodInvocation[frags.size()];
			for (int i = 0; i < frags.size(); i++) {
				added[i] = null;
				PrintStream printer = new PrintStream(new File("a.txt"));
				MyFragment frag = frags.elementAt(i);
				String predPath = frag.srcPath;
				String nextPath = frag.getNextFilePath();
				String nextProjectPath = nextPath.substring(0, nextPath.indexOf(frag.getNextVersionRepoID()) + frag.getNextVersionRepoID().length());
				//System.out.println("ProjectPath:" + nextProjectPath);
				
				String predProjectPath = predPath.substring(0, predPath.indexOf(frag.versionRepoID) + frag.versionRepoID.length());
				//System.out.println("ProjectPath:" + predProjectPath);
				HashSet<String> predSrcPathSet;
				HashSet<String> srcPathSet;
				SrcCollector predSrcCollector = new SrcCollector();
				SrcCollector nextSrcCollector = new SrcCollector();
				
				if (srcPaths.containsKey(predProjectPath)) {
					predSrcPathSet = srcPaths.get(predProjectPath);
				}
				else {
					predSrcCollector.analyzeSrcPaths(predProjectPath);
					predSrcPathSet = predSrcCollector.getSrcPathSet();
					srcPaths.put(predProjectPath, predSrcPathSet);
				}
				if (srcPaths.containsKey(nextProjectPath)) {
					srcPathSet = srcPaths.get(nextProjectPath);
				}
				else {
					nextSrcCollector.analyzeSrcPaths(nextProjectPath);
					srcPathSet = nextSrcCollector.getSrcPathSet();
					srcPaths.put(nextProjectPath, srcPathSet);
				}
				
				Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(frag, predPath, nextPath, srcPathSet, predSrcPathSet);
				if (methodInvokeList == null) {
					System.out.println("No added invoke");
					continue;
				}
				
				for (MethodInvocation methodInvo : methodInvokeList) {
					
					int lineNum = ((CompilationUnit) methodInvo.getRoot()).getLineNumber(methodInvo.getStartPosition());
					//System.out.println("%%%%%%%%%%");
					//System.out.println("AddedMethodInvo Line:" + lineNum + " InvoName:" + methodInvo);
					// resolve(get receiverFilePath and MethodSignature)
					IMethodBinding mBinding = methodInvo.resolveMethodBinding();
					// compare path with commonMethod
					String preferedSrcPath = nextSrcCollector.filePathToSrcPath(new File(nextPath));
					String invokedFilePath = nextSrcCollector.getFilePath(preferedSrcPath, mBinding, printer);
					if (invokedFilePath == null)
						continue;
					//System.out.println("InvokedFilePath:" + invokedFilePath);
					if(mBinding==null) {
						System.out.println("Binding is missing");
						continue;
					}
						
					
					
					//int lineNum = ((CompilationUnit) methodInvo.getRoot()).getLineNumber(methodInvo.getStartPosition());
					//System.out.println("%%%%%%%%%%");
					//System.out.println("AddedMethodInvo Line:" + lineNum + " InvoName:" + methodInvo);
					//IMethodBinding mBinding = methodInvo.resolveMethodBinding();
					
					String name = mBinding.getMethodDeclaration().getName();
					//System.out.println(name +" " + methodName);
					boolean flag = false;
					if (name.equals(methodName)) {
						//String preferedSrcPath = srcCollector.filePathToSrcPath(new File(nextPath));
						//String invokedFilePath = srcCollector.getFilePath(preferedSrcPath, mBinding, printer);
						if (invokedFilePath == null)
							continue;
						//System.out.println("InvokedFilePath:" + invokedFilePath);
						if (!invokedFilePath.equals(method.srcPath)) {
							continue;
						}
						
						for (int ii = 0; ii < i; ii++) {
							if (added[ii] == null)
								continue;
							int line1 = ((CompilationUnit) added[ii].getRoot()).getLineNumber(added[ii].getStartPosition());
							
							if (lineNum == line1) {
								//System.out.println("who?");
								flag = true;
								break;
							}
						}
						if (!flag) {
							added[i] = methodInvo;
							//System.out.println("hello:" + i + methodInvo);
							break;
						}
 					}
				}
				printer.close();
				
			}
			Vector<MyFragment> retFrag = new Vector<MyFragment>();
			for (int i = 0; i < frags.size(); i++) {
				if (added[i] == null) {
					System.out.println("heihei");
				}
				else {
					retFrag.add(frags.get(i));
				}
			}
			if (retFrag.size() >= 2) {
				
			}
			else {
				anotherunsatisfy.add(cnt);
				System.out.println("get one");
			}
		}
		
		System.out.println("Unsatisfy number:");
		System.out.println(anotherunsatisfy);

	}
	
	private Vector<MethodInvocation> detectAddedMethodInvoke(MyFragment frag,
			String predPath, String nextPath, HashSet<String> nextSrcPathSet, HashSet<String> predSrcPathSet) {
		
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, "1.8");
		
		MethodInvocationVisitor predVisitor = null, nextVisitor = null;
		File predFile = null, nextFile = null;
		CompilationUnit predcu = null, nextcu = null;

		ASTParser predParser = ASTParser.newParser(AST.JLS8);
		predParser.setKind(ASTParser.K_COMPILATION_UNIT);

		predFile = new File(predPath);
		if (!predFile.exists())
			System.out.println("predFile unExist");
		predParser.setSource(ReadFile.fileToCharArray(predFile));
		predParser.setResolveBindings(true);
		predParser.setBindingsRecovery(true);
		predParser.setUnitName(predFile.getName());
		if (predSrcPathSet.size() != 0) {
			predParser.setEnvironment(null, predSrcPathSet.toArray(new String[predSrcPathSet.size()]), null, true);
		} else {
			predParser.setEnvironment(null, null, null, true);
		}
		predcu = (CompilationUnit) predParser.createAST(null);
		predVisitor = new MethodInvocationVisitor(printer);
		predcu.accept(predVisitor);

		int predstart = frag.startLine, predend = frag.endLine;
		int nextStart, nextEnd;
		HashSet<MethodDeclaration> set = predVisitor.methodDeclareSet;
		String methodName = "";
		String className = "";
		List<SingleVariableDeclaration> paras = new Vector<SingleVariableDeclaration>();
		for (MethodDeclaration m : set) {
			int startline = ((CompilationUnit) m.getRoot()).getLineNumber(m.getStartPosition());
			int endline = ((CompilationUnit) m.getRoot()).getLineNumber(m.getStartPosition() + m.getLength());
			if (startline <= frag.startLine && endline >= frag.endLine) {
				IMethodBinding mBinding = m.resolveBinding();
				if (mBinding == null) {
					System.out.println("line 328 binding is missing");
					continue;
				}
				methodName = m.getName().toString();
				className = mBinding.getDeclaringClass().getQualifiedName();
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
	
		
		ASTParser nextParser = ASTParser.newParser(AST.JLS8);
		nextParser.setKind(ASTParser.K_COMPILATION_UNIT);
		nextFile = new File(nextPath);
		if (!nextFile.exists()) {
			System.out.println("nextFile unExist");
			return null;
		}		
		nextParser.setSource(ReadFile.fileToCharArray(nextFile));
		// for ResolveMethodBinding
		nextParser.setResolveBindings(true);
		nextParser.setBindingsRecovery(true);
		nextParser.setUnitName(nextFile.getName());

		if (nextSrcPathSet.size() != 0) {
			nextParser.setEnvironment(null, nextSrcPathSet.toArray(new String[nextSrcPathSet.size()]), null, true);
		} else {
			nextParser.setEnvironment(null, null, null, true);
		}
		// end of resolveMethodBinding
		nextcu = (CompilationUnit) nextParser.createAST(null);
		nextVisitor = new MethodInvocationVisitor(printer);
		nextcu.accept(nextVisitor);
		
		set = nextVisitor.methodDeclareSet;
		MethodDeclaration target = null;
		for (MethodDeclaration method : set) {
			String thisName = method.getName().toString();
			if (thisName.equals(methodName)) {
				System.out.println(thisName);
				IMethodBinding mBinding = method.resolveBinding();
				if (mBinding == null) {
					System.out.println("line 352 binding is null");
					continue;
				}
				String qualifiedClassName = mBinding.getDeclaringClass().getQualifiedName();
				System.out.println(qualifiedClassName + " " + className);
				if (qualifiedClassName == null || qualifiedClassName.equals("")) {
					continue;
				}
				if (qualifiedClassName.equals(className)) {
					List<SingleVariableDeclaration> thisparas = method.parameters();
					System.out.println(paras);
					System.out.println(thisparas);
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
						System.out.println("Find you!!!!!!!!!!!!!!");
						target = method;
						break;
					}
				}
			}
		}
		
		if (target == null) {
			System.out.println("what?");
			return null;
		}

		System.out.println("predMethodLineRange:" + predstart + "-" + predend);
		nextStart = ((CompilationUnit) target.getRoot()).getLineNumber(target.getStartPosition());
		nextEnd = ((CompilationUnit) target.getRoot()).getLineNumber(target.getStartPosition() + target.getLength());
		if (nextStart <= 0 || nextEnd <= 0)
			return null;
		System.out.println("nextLineRange:" + nextStart + "-" + nextEnd);

		Vector<String> predMethodInvoNames = new Vector<String>();
		for (int i = predstart; i <= predend; i++) {
			Vector<MethodInvocation> predMethodInvosThisLine = predVisitor.lineMethodInvoMap.get(i);
			if (predMethodInvosThisLine == null)
				continue;
			for (MethodInvocation tmp : predMethodInvosThisLine) {
				predMethodInvoNames.add(tmp.getName().toString());
			}
		}
		//System.out.println("predStrMethodInvoNames:");
		//System.out.println(predMethodInvoNames);

		Vector<MethodInvocation> addedMethodCalls = new Vector<MethodInvocation>();
		for (int i = nextStart; i <= nextEnd; i++) {
			Vector<MethodInvocation> nextMethodCallsThisLine = nextVisitor.lineMethodInvoMap.get(i);
			if (nextMethodCallsThisLine == null)
				continue;
			for (MethodInvocation tmp : nextMethodCallsThisLine) {
				if (!predMethodInvoNames.contains(tmp.getName().toString())) {
					addedMethodCalls.addElement(tmp);
					//System.out.println(tmp);
				}
			}
		}
		
		return addedMethodCalls;
		
	}
	
	public void getFilterInfo() throws Exception{
		inses.clear();
		File cloneFileFolder = new File(refactorPath);
		ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
		ArrayList<File> refactorCloneFileList = new ArrayList<File>();
		for (File cloneFile : cloneFileFolder.listFiles()) {
			String fileName = cloneFile.getName();
			if (fileName.contains("readable"))
				continue;
			if (fileName.contains("unrefactored"))
				unrefactorCloneFileList.add(cloneFile);
			else if (fileName.contains("refactored"))
				refactorCloneFileList.add(cloneFile);
		}
		Collections.sort(unrefactorCloneFileList, new SortByVersion());
		Collections.sort(refactorCloneFileList, new SortByVersion());
		System.out.println(refactorCloneFileList);
		System.out.println(unrefactorCloneFileList);
		Vector<RefactoredInstance> tmp = new Vector<RefactoredInstance>();
		for (File cloneFile : refactorCloneFileList) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
				tmp = (Vector<RefactoredInstance>) ois.readObject();
				ois.close();
			} catch (Exception e) {
				//throw e;
				e.printStackTrace();
			}
			System.out.println(tmp.size());
			for (RefactoredInstance ins : tmp) {
				inses.addElement(ins);
			}
		}
		System.out.println(inses.size());
		
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
				//System.out.println(str);
				String[] opt = str.split(", ");
				for (String s : opt) {
					//System.out.println(s);
					unsatisfy.add(Integer.parseInt(s));
				}
			}
		}
		r.close();
		//System.out.println(unsatisfy);
		
		filterFile = new File(filterFilePath + "2");
		r = new BufferedReader(new FileReader(filterFile));
		while ((str = r.readLine()) != null) {
			if (str.indexOf(",") < 0) {
				continue;
			}
			else {
				str = str.substring(1, str.length()-1);
				//System.out.println(str);
				String[] opt = str.split(", ");
				for (String s : opt) {
					//System.out.println(s);
					unsatisfy.add(Integer.parseInt(s));
				}
			}
		}
		r.close();
		System.out.println(unsatisfy);
		
		for (int cnt = 0; cnt < inses.size(); cnt++) {
			RefactoredInstance ins = inses.elementAt(cnt);
			if (unsatisfy.contains(cnt)) {
				continue;
			}
			
			//0.1
			String singleInstanceFolderPath = path1 + "refactor" + (cnt+1);
			File singleInstanceFolder = new File(singleInstanceFolderPath);
			if (!singleInstanceFolder.exists())
				singleInstanceFolder.mkdirs();
			Vector<MyFragment> frags = ins.frags;
			for (int i = 0; i < frags.size(); i++) {
				File fragFile = new File(
						singleInstanceFolderPath + "/cloneInstance" + (i + 1) + ".txt");
				PrintWriter pw = new PrintWriter(new FileWriter(fragFile));
				pw.println(frags.get(i).toString());
				pw.close();
			}
			File commonMethodFile = new File(
					singleInstanceFolderPath + "/commonMethod.txt");
			PrintWriter pw = new PrintWriter(new FileWriter(commonMethodFile));
			pw.println(ins.commonMethod.commonToString());
			pw.close();
			printOldAndNewFile(frags, singleInstanceFolderPath);
			
			//binding
			String bindingpath = singleInstanceFolderPath + "/bindingResult";
			
			for (int i = 0; i < frags.size(); i++) {
				MyFragment frag = frags.elementAt(i);
				PrintStream printer = new PrintStream(new File(bindingpath + (i+1) + ".txt"));
				String predPath = frag.srcPath;
				String nextPath = frag.getNextFilePath();
				//printer.println(predPath + "\n" + nextPath);
				String nextProjectPath = nextPath.substring(0, nextPath.indexOf(frag.getNextVersionRepoID()) + frag.getNextVersionRepoID().length());
				System.out.println("ProjectPath:" + nextProjectPath);
				printer.println("ProjectPath:" + nextProjectPath);
				
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
				
				Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(printer, frag, predPath, nextPath, srcPathSet);
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
				}
				printer.close();
			}
			
			//0.3
			Vector<MyFragment> fragtmp = new Vector<MyFragment> ();
			Vector<Double> rettmp = new Vector<Double>();
			for (int i = 0; i < ins.simis.size(); i++) {
				if (ins.simis.elementAt(i) >= 0.3) {
					fragtmp.addElement(ins.frags.get(i));
					rettmp.addElement(ins.simis.elementAt(i));
				}
			}
			
			if (rettmp.size() >= 2) {
				singleInstanceFolderPath = path3 + "refactor" + (cnt+1);
				singleInstanceFolder = new File(singleInstanceFolderPath);
				if (!singleInstanceFolder.exists())
					singleInstanceFolder.mkdirs();
				for (int i = 0; i < fragtmp.size(); i++) {
					File fragFile = new File(
							singleInstanceFolderPath + "/cloneInstance" + (i + 1) + ".txt");
					pw = new PrintWriter(new FileWriter(fragFile));
					pw.println(fragtmp.get(i).toString());
					pw.close();
					
				}
				commonMethodFile = new File(
						singleInstanceFolderPath + "/commonMethod.txt");
				pw = new PrintWriter(new FileWriter(commonMethodFile));
				pw.println(ins.commonMethod.commonToString());
				pw.close();
				printOldAndNewFile(fragtmp, singleInstanceFolderPath);
				
				//binding
				bindingpath = singleInstanceFolderPath + "/bindingResult";
				
				for (int i = 0; i < fragtmp.size(); i++) {
					MyFragment frag = fragtmp.elementAt(i);
					PrintStream printer = new PrintStream(new File(bindingpath + (i+1) + ".txt"));
					String predPath = frag.srcPath;
					String nextPath = frag.getNextFilePath();
					//printer.println(predPath + "\n" + nextPath);
					String nextProjectPath = nextPath.substring(0, nextPath.indexOf(frag.getNextVersionRepoID()) + frag.getNextVersionRepoID().length());
					System.out.println("ProjectPath:" + nextProjectPath);
					printer.println("ProjectPath:" + nextProjectPath);
					
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
					
					Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(printer, frag, predPath, nextPath, srcPathSet);
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
					}
					printer.close();
				}
			}
			
			
			//0.4
			fragtmp.clear();
			rettmp.clear();
			for (int i = 0; i < ins.simis.size(); i++) {
				if (ins.simis.elementAt(i) >= 0.4) {
					fragtmp.addElement(ins.frags.get(i));
					rettmp.addElement(ins.simis.elementAt(i));
				}
			}
			if (rettmp.size() >= 2) {
				singleInstanceFolderPath = path4 + "refactor" + (cnt+1);
				singleInstanceFolder = new File(singleInstanceFolderPath);
				if (!singleInstanceFolder.exists())
					singleInstanceFolder.mkdirs();
				for (int i = 0; i < fragtmp.size(); i++) {
					File fragFile = new File(
							singleInstanceFolderPath + "/cloneInstance" + (i + 1) + ".txt");
					pw = new PrintWriter(new FileWriter(fragFile));
					pw.println(fragtmp.get(i).toString());
					pw.close();
				}
				commonMethodFile = new File(
						singleInstanceFolderPath + "/commonMethod.txt");
				pw = new PrintWriter(new FileWriter(commonMethodFile));
				pw.println(ins.commonMethod.commonToString());
				pw.close();
				printOldAndNewFile(fragtmp, singleInstanceFolderPath);
				
				//binding
				bindingpath = singleInstanceFolderPath + "/bindingResult";
				for (int i = 0; i < fragtmp.size(); i++) {
					MyFragment frag = fragtmp.elementAt(i);
					PrintStream printer = new PrintStream(new File(bindingpath + (i+1) + ".txt"));
					String predPath = frag.srcPath;
					String nextPath = frag.getNextFilePath();
					//printer.println(predPath + "\n" + nextPath);
					String nextProjectPath = nextPath.substring(0, nextPath.indexOf(frag.getNextVersionRepoID()) + frag.getNextVersionRepoID().length());
					System.out.println("ProjectPath:" + nextProjectPath);
					printer.println("ProjectPath:" + nextProjectPath);
					
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
					
					Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(printer, frag, predPath, nextPath, srcPathSet);
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
					}
					printer.close();
			
				}
			}
			
			
			//0.5
			fragtmp.clear();
			rettmp.clear();
			for (int i = 0; i < ins.simis.size(); i++) {
				if (ins.simis.elementAt(i) >= 0.5) {
					fragtmp.addElement(ins.frags.get(i));
					rettmp.addElement(ins.simis.elementAt(i));
				}
			}
			if (rettmp.size() >= 2) {
				singleInstanceFolderPath = path5 + "refactor" + (cnt+1);
				singleInstanceFolder = new File(singleInstanceFolderPath);
				if (!singleInstanceFolder.exists())
					singleInstanceFolder.mkdirs();
				for (int i = 0; i < fragtmp.size(); i++) {
					File fragFile = new File(
							singleInstanceFolderPath + "/cloneInstance" + (i + 1) + ".txt");
					pw = new PrintWriter(new FileWriter(fragFile));
					pw.println(fragtmp.get(i).toString());
					pw.close();
				}
				commonMethodFile = new File(
						singleInstanceFolderPath + "/commonMethod.txt");
				pw = new PrintWriter(new FileWriter(commonMethodFile));
				pw.println(ins.commonMethod.commonToString());
				pw.close();
				printOldAndNewFile(fragtmp, singleInstanceFolderPath);
				
				//binding
				bindingpath = singleInstanceFolderPath + "/bindingResult";
				for (int i = 0; i < fragtmp.size(); i++) {
					MyFragment frag = fragtmp.elementAt(i);
					PrintStream printer = new PrintStream(new File(bindingpath + (i+1) + ".txt"));
					String predPath = frag.srcPath;
					String nextPath = frag.getNextFilePath();
					//printer.println(predPath + "\n" + nextPath);
					String nextProjectPath = nextPath.substring(0, nextPath.indexOf(frag.getNextVersionRepoID()) + frag.getNextVersionRepoID().length());
					System.out.println("ProjectPath:" + nextProjectPath);
					printer.println("ProjectPath:" + nextProjectPath);
					
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
					
					Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(printer, frag, predPath, nextPath, srcPathSet);
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
					}
					printer.close();
			
				}
			}
			
		}
		
	}

	private void outputCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(new Date()));
	}
	
	public void filterEqualInstance() {
		File cloneFileFolder = new File(refactorPath);
		ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
		ArrayList<File> refactorCloneFileList = new ArrayList<File>();
		for (File cloneFile : cloneFileFolder.listFiles()) {
			String fileName = cloneFile.getName();
			if (fileName.contains("readable"))
				continue;
			if (fileName.contains("unrefactored"))
				unrefactorCloneFileList.add(cloneFile);
			else if (fileName.contains("refactored"))
				refactorCloneFileList.add(cloneFile);
		}
		Collections.sort(unrefactorCloneFileList, new SortByVersion());
		Collections.sort(refactorCloneFileList, new SortByVersion());
		System.out.println(refactorCloneFileList);
		System.out.println(unrefactorCloneFileList);
		Vector<RefactoredInstance> tmp = new Vector<RefactoredInstance>();
		for (File cloneFile : refactorCloneFileList) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
				tmp = (Vector<RefactoredInstance>) ois.readObject();
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(tmp.size());
			for (RefactoredInstance ins : tmp) {
				inses.addElement(ins);
			}
		}			
		System.out.println(inses.size());
		
		Vector<Integer> unsatisfy = new Vector<Integer>();
		for (int i = 0; i < inses.size(); i++) {
			RefactoredInstance ins1 = inses.elementAt(i);
			//for (int j = 0; j < )
		}
	}

	public void filterSameInvocation() throws Exception{
		File cloneFileFolder = new File(refactorPath);
		ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
		ArrayList<File> refactorCloneFileList = new ArrayList<File>();
		for (File cloneFile : cloneFileFolder.listFiles()) {
			String fileName = cloneFile.getName();
			if (fileName.contains("readable"))
				continue;
			if (fileName.contains("unrefactored"))
				unrefactorCloneFileList.add(cloneFile);
			else if (fileName.contains("refactored"))
				refactorCloneFileList.add(cloneFile);
		}
		Collections.sort(unrefactorCloneFileList, new SortByVersion());
		Collections.sort(refactorCloneFileList, new SortByVersion());
		System.out.println(refactorCloneFileList);
		System.out.println(unrefactorCloneFileList);
		Vector<RefactoredInstance> tmp = new Vector<RefactoredInstance>();
		for (File cloneFile : refactorCloneFileList) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
				tmp = (Vector<RefactoredInstance>) ois.readObject();
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(tmp.size());
			for (RefactoredInstance ins : tmp) {
				inses.addElement(ins);
			}
		}			
		System.out.println(inses.size());
		
		Vector<Integer> unsatisfy = new Vector<Integer>();
		for (int cnt = 0; cnt < inses.size(); cnt++) {
			outputCurrentTime();
			System.out.println(cnt);
			RefactoredInstance ins = inses.elementAt(cnt);
			

			Vector<MyFragment> frags = ins.frags;
			MyFragment method = ins.commonMethod;
			HashSet<MethodDeclaration> set = null;
			if (fileDeclares.containsKey(method.srcPath)) {
				set = fileDeclares.get(method.srcPath);
			}
			else {
				//System.out.println("enheng?");
				set = getDeclare(method.srcPath);
			}
			//System.out.println("enheng?");
			String methodName = "";
			for (MethodDeclaration m : set) {
				int startline = ((CompilationUnit) m.getRoot()).getLineNumber(m.getStartPosition());
				int endline = ((CompilationUnit) m.getRoot()).getLineNumber(m.getStartPosition() + m.getLength());
				if (startline == method.startLine && endline == method.endLine) {
					methodName = m.getName().toString();
					break;
				}
			}
			if (methodName.equals("")) {
				System.out.println("Something wrong");
				unsatisfy.add(cnt);
				continue;
			}
			System.out.println(methodName);
			
			MethodInvocation[] added = new MethodInvocation[frags.size()];
			for (int i = 0; i < frags.size(); i++) {
				added[i] = null;
				PrintStream printer = new PrintStream(new File("a.txt"));
				MyFragment frag = frags.elementAt(i);
				String predPath = frag.srcPath;
				String nextPath = frag.getNextFilePath();
				//printer.println(predPath + "\n" + nextPath);
				String nextProjectPath = nextPath.substring(0, nextPath.indexOf(frag.getNextVersionRepoID()) + frag.getNextVersionRepoID().length());
				//System.out.println("ProjectPath:" + nextProjectPath);
				printer.println("ProjectPath:" + nextProjectPath);
				
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
				
				Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(printer, frag, predPath, nextPath, srcPathSet);
				if (methodInvokeList == null) {
					System.out.println("No added invoke");
					continue;
				}

				for (MethodInvocation methodInvo : methodInvokeList) {
					
					int lineNum = ((CompilationUnit) methodInvo.getRoot()).getLineNumber(methodInvo.getStartPosition());
					printer.println("%%%%%%%%%%");
					printer.println("AddedMethodInvo Line:" + lineNum + " InvoName:" + methodInvo);
					// resolve(get receiverFilePath and MethodSignature)
					IMethodBinding mBinding = methodInvo.resolveMethodBinding();
					if(mBinding==null)
						continue;
					String name = mBinding.getMethodDeclaration().getName();
					boolean flag = false;
					if (name.equals(methodName)) {
						for (int ii = 0; ii < i; ii++) {
							if (added[ii] == null)
								continue;
							int line1 = ((CompilationUnit) added[ii].getRoot()).getLineNumber(added[ii].getStartPosition());
							if (lineNum == line1) {
								flag = true;
								break;
							}
						}
						if (!flag) {
							added[i] = methodInvo;
							break;
						}
 					}
				}
				printer.close();
				
			}
			Vector<MyFragment> retFrag = new Vector<MyFragment>();
			for (int i = 0; i < frags.size(); i++) {
				if (added[i] == null) {
					System.out.println("heihei");
				}
				else {
					retFrag.add(frags.get(i));
					//System.out.println(added[i].toString());
				}
			}
			if (retFrag.size() >= 2) {
				
			}
			else {
				unsatisfy.add(cnt);
				System.out.println("get one");
			}
		}
		
		System.out.println("Unsatisfy number:");
		System.out.println(unsatisfy);
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(filterFilePath)));
		w.write("Unsatisfy number:\n");
		w.write(unsatisfy.toString());
		w.write("\n");
		w.close();
		
	}
	
	private HashSet<MethodDeclaration> getDeclare(String srcPath) {
		//System.out.println(srcPath);
		File methodFile = new File(srcPath);
		MethodInvocationVisitor v = null;
		CompilationUnit cu = null;
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, "1.8");
		//System.out.println("hello");
		parser.setSource(ReadFile.fileToCharArray(methodFile));
		//System.out.println("hello");
		cu = (CompilationUnit) parser.createAST(null);
		//System.out.println("hello");
		v = new MethodInvocationVisitor();
		cu.accept(v);
		
		return v.methodDeclareSet;
	}

	public void getInfo() throws Exception {
		File cloneFileFolder = new File(refactorPath);
		ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
		ArrayList<File> refactorCloneFileList = new ArrayList<File>();
		for (File cloneFile : cloneFileFolder.listFiles()) {
			String fileName = cloneFile.getName();
			if (fileName.contains("readable"))
				continue;
			if (fileName.contains("unrefactored"))
				unrefactorCloneFileList.add(cloneFile);
			else if (fileName.contains("refactored"))
				refactorCloneFileList.add(cloneFile);
		}
		Collections.sort(unrefactorCloneFileList, new SortByVersion());
		Collections.sort(refactorCloneFileList, new SortByVersion());
		System.out.println(refactorCloneFileList);
		System.out.println(unrefactorCloneFileList);
		Vector<RefactoredInstance> tmp = new Vector<RefactoredInstance>();
		for (File cloneFile : refactorCloneFileList) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
				tmp = (Vector<RefactoredInstance>) ois.readObject();
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(tmp.size());
			for (RefactoredInstance ins : tmp) {
				inses.addElement(ins);
			}
		}
		System.out.println(inses.size());
		int cnty = 0;
		for (int cnt = 0; cnt < inses.size(); cnt++) {
			RefactoredInstance ins = inses.elementAt(cnt);
			
			/*
			//0.1
			String singleInstanceFolderPath = path1 + "refactor" + (cnt+1);
			File singleInstanceFolder = new File(singleInstanceFolderPath);
			if (!singleInstanceFolder.exists())
				singleInstanceFolder.mkdirs();
			Vector<MyFragment> frags = ins.frags;
			for (int i = 0; i < frags.size(); i++) {
				File fragFile = new File(
						singleInstanceFolderPath + "/cloneInstance" + (i + 1) + ".txt");
				PrintWriter pw = new PrintWriter(new FileWriter(fragFile));
				pw.println(frags.get(i).toString());
				pw.close();
			}
			File commonMethodFile = new File(
					singleInstanceFolderPath + "/commonMethod.txt");
			//PrintWriter pw = null;
			PrintWriter pw = new PrintWriter(new FileWriter(commonMethodFile));
			pw.println(ins.commonMethod.commonToString());
			pw.close();
			//printOldAndNewFile(frags, singleInstanceFolderPath);
			
			//binding
			String bindingpath = singleInstanceFolderPath + "/bindingResult";
			
			for (int i = 0; i < frags.size(); i++) {
				MyFragment frag = frags.elementAt(i);
				PrintStream printer = new PrintStream(new File(bindingpath + (i+1) + ".txt"));
				String predPath = frag.srcPath;
				String nextPath = frag.getNextFilePath();
				//printer.println(predPath + "\n" + nextPath);
				String nextProjectPath = nextPath.substring(0, nextPath.indexOf(frag.getNextVersionRepoID()) + frag.getNextVersionRepoID().length());
				System.out.println("ProjectPath:" + nextProjectPath);
				printer.println("ProjectPath:" + nextProjectPath);
				
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
				
				Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(printer, frag, predPath, nextPath, srcPathSet);
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
				}
				printer.close();
			}
			*/
			//0.4
			Vector<MyFragment> fragtmp = new Vector<MyFragment> ();
			Vector<Double> rettmp = new Vector<Double>();
			for (int i = 0; i < ins.simis.size(); i++) {
				if (ins.simis.elementAt(i) >= 0.5) {
					fragtmp.addElement(ins.frags.get(i));
					rettmp.addElement(ins.simis.elementAt(i));
				}
			}
			
			if (rettmp.size() >= 2) {
				cnty ++;
				System.out.println(cnty);
				/*
				singleInstanceFolderPath = path3 + "refactor" + (cnt+1);
				singleInstanceFolder = new File(singleInstanceFolderPath);
				if (!singleInstanceFolder.exists())
					singleInstanceFolder.mkdirs();
				for (int i = 0; i < fragtmp.size(); i++) {
					File fragFile = new File(
							singleInstanceFolderPath + "/cloneInstance" + (i + 1) + ".txt");
					pw = new PrintWriter(new FileWriter(fragFile));
					pw.println(fragtmp.get(i).toString());
					pw.close();
					*/
				}
				/*commonMethodFile = new File(
						singleInstanceFolderPath + "/commonMethod.txt");
				pw = new PrintWriter(new FileWriter(commonMethodFile));
				pw.println(ins.commonMethod.commonToString());
				pw.close();
				printOldAndNewFile(fragtmp, singleInstanceFolderPath);
				*//*
				//binding
				bindingpath = singleInstanceFolderPath + "/bindingResult";
				
				for (int i = 0; i < fragtmp.size(); i++) {
					MyFragment frag = fragtmp.elementAt(i);
					PrintStream printer = new PrintStream(new File(bindingpath + (i+1) + ".txt"));
					String predPath = frag.srcPath;
					String nextPath = frag.getNextFilePath();
					//printer.println(predPath + "\n" + nextPath);
					String nextProjectPath = nextPath.substring(0, nextPath.indexOf(frag.getNextVersionRepoID()) + frag.getNextVersionRepoID().length());
					System.out.println("ProjectPath:" + nextProjectPath);
					printer.println("ProjectPath:" + nextProjectPath);
					
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
					
					Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(printer, frag, predPath, nextPath, srcPathSet);
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
					}
					printer.close();
				}*/
			}
			
			/*
			//0.5
			fragtmp.clear();
			rettmp.clear();
			for (int i = 0; i < ins.simis.size(); i++) {
				if (ins.simis.elementAt(i) >= 0.5) {
					fragtmp.addElement(ins.frags.get(i));
					rettmp.addElement(ins.simis.elementAt(i));
				}
			}
			if (rettmp.size() >= 2) {
				singleInstanceFolderPath = path5 + "refactor" + (cnt+1);
				singleInstanceFolder = new File(singleInstanceFolderPath);
				if (!singleInstanceFolder.exists())
					singleInstanceFolder.mkdirs();
				for (int i = 0; i < fragtmp.size(); i++) {
					File fragFile = new File(
							singleInstanceFolderPath + "/cloneInstance" + (i + 1) + ".txt");
					pw = new PrintWriter(new FileWriter(fragFile));
					pw.println(fragtmp.get(i).toString());
					pw.close();
				}
				commonMethodFile = new File(
						singleInstanceFolderPath + "/commonMethod.txt");
				pw = new PrintWriter(new FileWriter(commonMethodFile));
				pw.println(ins.commonMethod.commonToString());
				pw.close();
				printOldAndNewFile(fragtmp, singleInstanceFolderPath);
				
				//binding
				bindingpath = singleInstanceFolderPath + "/bindingResult";
				for (int i = 0; i < fragtmp.size(); i++) {
					MyFragment frag = fragtmp.elementAt(i);
					PrintStream printer = new PrintStream(new File(bindingpath + (i+1) + ".txt"));
					String predPath = frag.srcPath;
					String nextPath = frag.getNextFilePath();
					//printer.println(predPath + "\n" + nextPath);
					String nextProjectPath = nextPath.substring(0, nextPath.indexOf(frag.getNextVersionRepoID()) + frag.getNextVersionRepoID().length());
					System.out.println("ProjectPath:" + nextProjectPath);
					printer.println("ProjectPath:" + nextProjectPath);
					
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
					
					Vector<MethodInvocation> methodInvokeList = detectAddedMethodInvoke(printer, frag, predPath, nextPath, srcPathSet);
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
					}
					printer.close();
			
				}
			}
			
		}
		*/
		
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

	private Vector<MethodInvocation> detectAddedMethodInvoke(PrintStream printer, MyFragment frag, String predPath, String nextPath, HashSet<String> srcPathSet) throws Exception {
		int[] linemap = buildLineMap(nextPath, predPath);
		if (linemap == null) {
			System.out.println("linemap is null");
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