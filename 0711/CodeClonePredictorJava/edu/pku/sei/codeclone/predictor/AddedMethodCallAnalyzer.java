package edu.pku.sei.codeclone.predictor;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.MethodCallExpr;

public class AddedMethodCallAnalyzer {

	public static String unrefactorFileLabel = "unrefactored";
	public static String refactorFileLabel = "refactored";

	public static List<RefactoredInstance> refactorInstances = new Vector<RefactoredInstance>();

	public static void main(String[] args) throws Exception {

		String cloneDataPath = "/root/Projects/newResult/refactorInstances/emf/";
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
		System.out.println("RefactorInstances:" + refactorInstances.size());
		for (int i = 0; i < refactorInstances.size(); i++) {
			System.out.println("Ins " + i + ":");
			String methodVersion = refactorInstances.get(i).commonMethod.versionRepoID;
			for (int j = 0; j < refactorInstances.get(i).frags.size(); j++) {
				MyFragment frag = refactorInstances.get(i).frags.elementAt(j);
				Vector<MethodCallExpr> addedMethodCalls = detectAddedMethodInvoke(frag, methodVersion);
				System.out.println("Frag " + j + " add method invokes:");
				System.out.println(addedMethodCalls);
				if (addedMethodCalls != null) {
					for (MethodCallExpr methodCall : addedMethodCalls) {
						
					}
				}
			}
		}
	}

	private static Vector<MethodCallExpr> detectAddedMethodInvoke(MyFragment frag, String nextVersionRepoID)
			throws Exception {
		String predPath = frag.srcPath;
		String predVersion = frag.formatVersionRepoName(frag.versionRepoID);
		String nextVersion = frag.formatVersionRepoName(nextVersionRepoID);
		String nextPath = predPath.replace("/" + frag.versionRepoID + "/", "/" + nextVersionRepoID + "/");

		predPath = predPath.replace("/" + frag.versionRepoID + "/", "/" + predVersion + "/");
		nextPath = nextPath.replace("/" + nextVersionRepoID + "/", "/" + nextVersion + "/");
		// System.out.println(predPath + " " + nextPath);
		int[] linemap = buildLineMap(nextPath, predPath);
		if (linemap == null) {
			return null;
		}
		if (linemap.length <= frag.startLine - 1) {
			System.out.println("Wrong here. frag:\n" + frag);
			System.out.println("linemap:\n" + linemap.length);
			return new Vector<MethodCallExpr>();
		}
		int nextStart, nextEnd;
		MethodVisitor predv = null;
		MethodVisitor nextv = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(predPath);
			CompilationUnit cu = JavaParser.parse(in);
			predv = new MethodVisitor();
			predv.visit(cu, null);
			in.close();

			in = new FileInputStream(nextPath);
			cu = JavaParser.parse(in);
			nextv = new MethodVisitor();
			nextv.visit(cu, null);
			in.close();
		} catch (Exception ex) {
			// ex.printStackTrace();
			return null;
		} catch (Error err) {
			System.out.println("Error happened: \n" + predPath + " or " + nextPath);
			return null;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		HashMap<String, MethodDeclaration> predMethod = predv.methodDeclare;
		int predstart = frag.startLine, predend = frag.endLine;
		for (String key : predMethod.keySet()) {
			MethodDeclaration method = predMethod.get(key);
			if (method.getBeginLine() <= frag.startLine && method.getEndLine() >= frag.endLine) {
				predstart = method.getBeginLine();
				predend = method.getEndLine();
				break;
			}
		}
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

		Vector<MethodCallExpr> predMethodCalls = new Vector<MethodCallExpr>();
		for (int i = predstart; i <= predend; i++) {
			Vector<MethodCallExpr> predMethodCallsThisLine = predv.methodCallExpr.get(i);
			if (predMethodCallsThisLine == null)
				continue;
			for (MethodCallExpr tmp : predMethodCallsThisLine) {
				predMethodCalls.addElement(tmp);
			}
		}

		Vector<MethodCallExpr> addedMethodCalls = new Vector<MethodCallExpr>();
		for (int i = nextStart; i <= nextEnd; i++) {
			Vector<MethodCallExpr> nextMethodCallsThisLine = nextv.methodCallExpr.get(i);
			if (nextMethodCallsThisLine == null)
				continue;
			for (MethodCallExpr tmp : nextMethodCallsThisLine) {
				if (!predMethodCalls.contains(tmp)) {
					addedMethodCalls.addElement(tmp);
				}
			}
		}
		return addedMethodCalls;
	}

	public static int[] buildLineMap(String curPath, String predPath) {

		File tempFile = new File(curPath);
		if (!tempFile.exists()) {
			return null;
		}
		tempFile = new File(predPath);
		if (!tempFile.exists()) {
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