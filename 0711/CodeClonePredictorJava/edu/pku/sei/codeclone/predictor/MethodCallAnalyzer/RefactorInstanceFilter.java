package edu.pku.sei.codeclone.predictor.MethodCallAnalyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import edu.pku.sei.codeclone.predictor.ChangeGenerator;
import edu.pku.sei.codeclone.predictor.ForGZTest.MethodVisitor;
import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.MyVersionList;
import edu.pku.sei.codeclone.predictor.RefactoredInstance;

public class RefactorInstanceFilter {
	public static PrintStream printer = null;
	static String lineSep = System.getProperty("line.separator");
	static String fileSep = System.getProperty("file.separator");
	public static String unrefactorFileLabel = "unrefactored";
	public static String refactorFileLabel = "refactored";

	public static List<RefactoredInstance> refactorInstances = new Vector<RefactoredInstance>();

	public static void main(String[] args) throws Exception {
		printer = new PrintStream(new File("/root/Desktop/out-emf.txt"));
		String cloneDataPath = "/root/Projects/newResult/refactorInstances/emf/";
		
//		printer = new PrintStream(new File("/root/Desktop/out-es.txt"));
//		String cloneDataPath = "/root/Projects/newResult/refactorInstances/es/";
		
//		printer = new PrintStream(new File("/root/Desktop/out-eclipse.txt"));
//		String cloneDataPath = "/root/Projects/newResult/refactorInstances/eclipse/";
		
		File cloneFileFolder = new File(cloneDataPath);
		ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
		ArrayList<File> refactorCloneFileList = new ArrayList<File>();
		for (File cloneFile : cloneFileFolder.listFiles()) {
			if (cloneFile.isDirectory())
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
		System.out.println("RefactorList:" + refactorCloneFileList);
		Collections.sort(refactorCloneFileList, new SortByVersion());
		for (File cloneFile : refactorCloneFileList) {
			Vector<RefactoredInstance> refactoredInsList = new Vector<RefactoredInstance>();
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
			refactoredInsList = (Vector<RefactoredInstance>) ois.readObject();
			for (RefactoredInstance ins : refactoredInsList) {
				refactorInstances.add(ins);
			}
			ois.close();
		}
		printer.println("RefactorInstances:" + refactorInstances.size());
		ArrayList<Integer> filteredInstances = new ArrayList<Integer>();

		for (int i = 0; i < refactorInstances.size(); i++) {
			printer.println("********RefactorInstance" + i + "********");
			MyFragment commonMethod = refactorInstances.get(i).commonMethod;
			String methodVersion = commonMethod.formatVersionRepoName(commonMethod.versionRepoID);

			Vector<MyFragment> newFrags = new Vector<MyFragment>();
			for (int j = 0; j < refactorInstances.get(i).frags.size(); j++) {
				MyFragment frag = refactorInstances.get(i).frags.elementAt(j);
				printer.println("------------");
				printer.println("Frag" + j);
				Vector<MethodInvocation> addedMethodInvos = detectAddedMethodInvoke(frag, methodVersion);
				if (addedMethodInvos != null) {
					for (MethodInvocation methodInvo : addedMethodInvos) {
						int lineNum = ((CompilationUnit) methodInvo.getRoot())
								.getLineNumber(methodInvo.getStartPosition());
						printer.println("%%%%%%%%%%");
						printer.println("AddedMethodInvo Line:" + lineNum + " InvoName:" + methodInvo);
						// resolve(get receiverFilePath and MethodSignature)
						IMethodBinding mBinding = methodInvo.resolveMethodBinding();
						// compare path with commonMethod
						String nextFilePathOfFrag=getNextPath(frag,methodVersion);
						String preferedSrcPath=SrcCollector.filePathToSrcPath(new File(nextFilePathOfFrag));
						String invokedFilePath = SrcCollector.getFilePath(preferedSrcPath,mBinding,printer);
						printer.println("@@@@@@");
						printer.println("InvokedFilePath:" + invokedFilePath);
						String comMethodFilePath=commonMethod.srcPath.replace("/" + commonMethod.versionRepoID + "/", "/" + methodVersion + "/");
						printer.println("CommonMFilePath:" + comMethodFilePath);
						printer.println("CommonMLineRange:" + commonMethod.startLine + "-" + commonMethod.endLine);
						if (comMethodFilePath.equals(invokedFilePath))
							newFrags.add(frag);
						//compare1: filePath containing invokedMethod
						//compare2: the declation of invokedMethod
						//IMethodBinding.getMethodDeclaration().toString()
						//e.g.,public String getStr(int i)
						
					} // end of addedMethodInvos
				} // end of addedMethodInvos not null
			} // end of frags
			if (newFrags.size() < 2)
				filteredInstances.add(i);
		} // end of refactoredInstances
		printer.println("FilteredRefactorInstances" + filteredInstances);
	}
	
	private static String getNextPath(MyFragment frag, String nextVersionRepoID) {
		String predPath = frag.srcPath;
		String predVersion = frag.formatVersionRepoName(frag.versionRepoID);
		String nextVersion = frag.formatVersionRepoName(nextVersionRepoID);
		String nextPath = predPath.replace("/" + frag.versionRepoID + "/", "/" + nextVersionRepoID + "/");

		predPath = predPath.replace("/" + frag.versionRepoID + "/", "/" + predVersion + "/");
		nextPath = nextPath.replace("/" + nextVersionRepoID + "/", "/" + nextVersion + "/");
		return nextPath;
	}

	private static Vector<MethodInvocation> detectAddedMethodInvoke(MyFragment frag, String nextVersionRepoID) {
		String predPath = frag.srcPath;
		String predVersion = frag.formatVersionRepoName(frag.versionRepoID);
		String nextVersion = frag.formatVersionRepoName(nextVersionRepoID);
		String nextPath = predPath.replace("/" + frag.versionRepoID + "/", "/" + nextVersionRepoID + "/");

		predPath = predPath.replace("/" + frag.versionRepoID + "/", "/" + predVersion + "/");
		nextPath = nextPath.replace("/" + nextVersionRepoID + "/", "/" + nextVersion + "/");

		printer.println("predPath:" + predPath);
		printer.println("nextPath:" + nextPath);
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
		if(file.exists())
			printer.println("predFile unExist");
		parser.setSource(ReadFile.fileToCharArray(file));
		cu = (CompilationUnit) parser.createAST(null);
		predVisitor = new MethodInvocationVisitor(printer);
		cu.accept(predVisitor);
		printer.println("end of predVisitor");

		file = new File(nextPath);
		if(file.exists())
			printer.println("nextFile unExist");
		parser.setSource(ReadFile.fileToCharArray(file));
		//for ResolveMethodBinding
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setUnitName(file.getName());
		String tmpNextVersion = "/" + frag.formatVersionRepoName(nextVersionRepoID) + "/";
		int projectStartIndex = nextPath.indexOf(tmpNextVersion) + tmpNextVersion.length();
		String projectPath = nextPath.substring(0, projectStartIndex);
		printer.println("ProjectPath:" + projectPath);
		SrcCollector.analyzeSrcPaths(projectPath);
		HashSet<String> srcPathSet = SrcCollector.srcPathSet;
		 printer.println("SrcPathArray:");
		 printer.println(Arrays.toString(srcPathSet.toArray(new
		 String[srcPathSet.size()])));
		 

		if (srcPathSet.size() != 0) {
			parser.setEnvironment(null, srcPathSet.toArray(new String[srcPathSet.size()]), null, true);
		} else {
			parser.setEnvironment(null, null, null, true);
		}
		// end of resolveMethodBinding
		cu = (CompilationUnit) parser.createAST(null);
		nextVisitor = new MethodInvocationVisitor(printer);
		cu.accept(nextVisitor);
		printer.println("end of nextVisitor");

		HashSet<MethodDeclaration> predMethodDecSet = predVisitor.methodDeclareSet;
		int predstart = frag.startLine, predend = frag.endLine;
//		printer.println("predFragLineRange:" + predstart + "-" + predend);
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
//		printer.println("predMethodLineRange:" + predstart + "-" + predend);
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
//		printer.println("nextLineRange:" + nextStart + "-" + nextEnd);

		// for(int i:predVisitor.lineMethodInvoMap.keySet()){
		// printer.println("Line:"+i);
		// printer.println(predVisitor.lineMethodInvoMap.get(i));
		// }

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

	public static int[] buildLineMap(String curPath, String predPath) {
		File tempFile = new File(curPath);
		if (!tempFile.exists()) {
			printer.println("nextFile unExist in buildLineMap");
			return null;
		}
		tempFile = new File(predPath);
		if (!tempFile.exists()) {
			printer.println("predFile unExist in buildLineMap");
			return null;
		}

		int[] linemap = MyVersionList.lineMapTable.get(curPath);
		if (linemap == null) {
			List<String> currentValue = ChangeGenerator.getSrc(curPath);
			List<String> predValue = ChangeGenerator.getSrc(predPath);
			Patch p = DiffUtils.diff(predValue, currentValue);
			List<Delta> deltas = p.getDeltas();

			for (int i = 0; i < deltas.size(); i++) {
				for (int j = i + 1; j < deltas.size(); j++) {
					if (deltas.get(i).getOriginal().getPosition() < deltas.get(j).getOriginal().getPosition()) {
						Delta d = deltas.get(i);
						deltas.set(i, deltas.get(j));
						deltas.set(j, d);
					}
				}
			}

			linemap = new int[predValue.size() + 1];
			int index1 = 0;
			int index2 = 0;

			for (int k = deltas.size() - 1; k >= 0; k--) {
				Delta del = deltas.get(k);
				int lineNumber = del.getOriginal().getPosition();
				int linesOld = del.getOriginal().getLines().size();
				int linesNew = del.getRevised().getLines().size();
				for (int i = index1; i < lineNumber; i++) {
					linemap[i] = index2;
					index2++;
				}
				index1 = lineNumber;
				if (del.getType().equals(Delta.TYPE.INSERT)) {
					index2 += linesNew;
				} else if (del.getType().equals(Delta.TYPE.DELETE)) {
					for (int i = index1; i < index1 + linesOld; i++) {
						linemap[i] = -1;
					}
					index1 += linesOld;
				} else if (del.getType().equals(Delta.TYPE.CHANGE)) {
					for (int i = index1; i < index1 + linesOld; i++) {
						linemap[i] = -1;
					}
					index1 += linesOld;
					index2 += linesNew;
				}
			}
			for (int i = index1; i < predValue.size(); i++) {
				linemap[i] = index2;
				index2++;
			}
			MyVersionList.lineMapTable.put(curPath, linemap);
		}
		return linemap;
	}
}