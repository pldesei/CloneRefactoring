package edu.pku.sei.codeclone.predictor;

import java.io.*;
import java.util.*;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import waterloo.*;

public class Improve {
	
	static String unrefactorFileLabel = "unrefactored";
	static String refactorFileLabel = "refactored";
	
	public static Vector<RefactoredInstance> refactorInstances = new Vector<RefactoredInstance>();
	public static List<MyCloneClass> unrefactorInstances = new Vector<MyCloneClass>();
	
	public static String baseVersionPath = "/root/Projects/org.eclipse.emfFilter/";
	public static HashMap<String, VersionMethods> versions = new HashMap<String, VersionMethods>();
	public static HashMap<String, RepoStructure> repos = new HashMap<String, RepoStructure>();
	
	
	public static void main(String[] args) throws Exception{
		String cloneDataPath = "/root/Projects/newResult/refactorInstances/emf/";
		File cloneFileFolder = new File(cloneDataPath);
		ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
		ArrayList<File> refactorCloneFileList = new ArrayList<File>();
		for (File cloneFile : cloneFileFolder.listFiles()) {
			if(cloneFile.isDirectory())
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
		System.out.println("UnrefactorList:" + unrefactorCloneFileList);
		System.out.println("RefactorList:" + refactorCloneFileList);
		Collections.sort(refactorCloneFileList, new SortByVersion());
		/*for (File cloneFile : unrefactorCloneFileList) {
			List<MyCloneClass> unrefactoredCloneList = new Vector<MyCloneClass>();
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
			unrefactoredCloneList = (List<MyCloneClass>) ois.readObject();
		}*/
		for (File cloneFile : refactorCloneFileList) {
			Vector<RefactoredInstance> refactoredInsList = new Vector<RefactoredInstance>();
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
			refactoredInsList = (Vector<RefactoredInstance>) ois.readObject();
			for (RefactoredInstance ins : refactoredInsList) {
				refactorInstances.add(ins);
			}
		}
		System.out.println("RefactorInstances:" + refactorInstances.size());
		//refactorInstances = getNewResult(refactorInstances);
		//refactorInstances = filterEqualFunction(refactorInstances);
		refactorInstances = filterWrongReference(refactorInstances);
		System.out.println("After Fileter:" + refactorInstances.size());
	}
	
	public static Vector<RefactoredInstance> filterWrongReference(Vector<RefactoredInstance> inses) throws Exception{
		Vector<RefactoredInstance> ret = new Vector<RefactoredInstance>();
		for (int i = 0; i < inses.size(); i++) {
			Vector<MyFragment> frags = inses.get(i).frags;
			MyFragment methodFrag= inses.get(i).commonMethod;
			String methodVersion = methodFrag.formatVersionRepoName(methodFrag.versionRepoID);
			//System.out.println(methodFrag.getFilePath());
			if (!versions.containsKey(methodVersion)) {
				getMethodDeclaration(methodVersion);
				getRepos(methodVersion);
			}
			VersionMethods thisVersion = versions.get(methodVersion);
			MethodDeclaration method = thisVersion.allMethodsInLine.get(methodFrag.getFilePath()).get(methodFrag.startLine);
			String methodName = method.getName();
			Vector<MyFragment> newFrags = new Vector<MyFragment>();
			Vector<Double> simis = new Vector<Double>();
			for (int j = 0; j < frags.size(); j++) {
				String path = getInvokePath(frags.get(j), methodVersion, methodName);
				if (path == null || path.equals(methodFrag.getFilePath())) {
					newFrags.add(frags.get(j));
					simis.add(inses.get(i).simis.elementAt(j));
				}
			}
			if (newFrags.size() == frags.size()) {
				ret.add(inses.get(i));
			}
			else {
				if (newFrags.size() >= 2) {
					RefactoredInstance ins = new RefactoredInstance(newFrags, methodFrag, simis);
					ret.add(ins);
				}
				else {
					System.out.println("Filter:" + (i+1));
					
					FileWriter w = new FileWriter(new File("/root/Projects/newResult/es.txt"), true);
					w.write("Filter:" + (i+1)+"\n");
					w.close();
				}
			}
		}
		return ret;
	}
	
	public static String getInvokePath(MyFragment frag, String version, String methodName) {
		String fragNewPath = frag.getFilePath().replace(frag.formatVersionRepoName(frag.versionRepoID), version);
		RepoStructure thisRepo = repos.get(version);
		VersionMethods thisVersion = versions.get(version);
		HashMap<String, MethodDeclaration> fragFileDeclare = thisVersion.allMethods.get(fragNewPath);
		if (fragFileDeclare.containsKey(methodName)) {
			return fragNewPath;
		}
		Vector<JavaClass> thisClass = new Vector<JavaClass>();
		for (JavaClass jc : thisRepo.classes) {
			if (jc.filePath.equals(fragNewPath)) {
				thisClass.add(jc);
				break;
			}
		}
		for (JavaClass thisclass : thisClass) {
			ClassNode thisFamily = null;
			for (ClassNode cn : thisRepo.classFamilies) {
				if (cn.containClass(thisclass)) {
					thisFamily = cn;
					break;
				}
			}
			if (thisFamily == null) {
				
			}
			else {
				String ret = checkWhere(thisFamily, methodName, thisVersion.allMethods);
				if (ret != null)
					return ret;
			}
		}
		
		return null;
	}
	
	public static String checkWhere(ClassNode family, String methodName, HashMap<String, HashMap<String, MethodDeclaration>> allMethods) {
		JavaClass thisclass = family.curJavaClass;
		HashMap<String, MethodDeclaration> declare = allMethods.get(thisclass.filePath);
		if (declare.containsKey(methodName)) {
			return thisclass.filePath;
		}
		else {
			if (family.children.size() > 0) {
				for (ClassNode cn : family.children)
					return checkWhere(cn, methodName, allMethods);
			}
		}
		return null;
	}
	
	public static void getRepos(String methodVersion) {
		String basePath = baseVersionPath + methodVersion;
		RepoStructure str = new RepoStructure(basePath);
		str.analyzeStructure();
		repos.put(methodVersion, str);
	}
	
	private static void getMethodDeclaration(String version) {
		String basePath = baseVersionPath + version;
		HashMap<String, HashMap<String, MethodDeclaration>> tmp = new HashMap<String, HashMap<String, MethodDeclaration>>();
		HashMap<String, HashMap<Integer, MethodDeclaration>> tmpInLine = new HashMap<String, HashMap<Integer, MethodDeclaration>>();
		VersionMethods thisVersion = new VersionMethods(version, tmp, tmpInLine);
		versions.put(version, thisVersion);
		
		File file = new File(basePath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		for (File tmpFile : files) {
			judge(tmpFile, version);
		}
	}
	
	public static void judge(File tmpFile, String version) {
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
				VersionMethods methods = versions.get(version);
				HashMap<String, HashMap<String, MethodDeclaration>> tmp = methods.allMethods;
				HashMap<String, HashMap<Integer, MethodDeclaration>> tmpInLine = methods.allMethodsInLine;
				MethodVisitor v = null;
				FileInputStream in = null;
				try {
					in = new FileInputStream(tmpFile.getAbsolutePath());
					CompilationUnit cu = JavaParser.parse(in);
					v = new MethodVisitor();
					v.visit(cu, null);
					in.close();
				}
				catch (Exception ex) {
					//ex.printStackTrace();
					return;
				}
				catch (Error err) {
					System.out.println("Error happened: \n" + tmpFile.getAbsolutePath());
					return;
				}
				finally {
					try {
						in.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				tmp.put(tmpFile.getAbsolutePath(), v.methodDeclare);
				tmpInLine.put(tmpFile.getAbsolutePath(), v.methodDeclareInLine);
			}
		}
	}
	
	private static Vector<RefactoredInstance> getNewResult(Vector<RefactoredInstance> refactorInstances) throws Exception {
		Vector<RefactoredInstance> ret = new Vector<RefactoredInstance>();
		for (int i = 0; i < refactorInstances.size(); i++) {
			if (i != 95)
				ret.add(refactorInstances.get(i));
			else {
				System.out.println(refactorInstances.get(i).frags.elementAt(0).startLine);
				System.out.println(refactorInstances.get(i).frags.elementAt(0).endLine);
			}
		}
		System.out.println(ret.size());
		/*String refactoredOutputPath = "/root/Projects/newResult/refactorInstances/es/refactored1-10.txt";
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(refactoredOutputPath)));
		oos.writeObject(ret);
		oos.close();*/
		return ret;
	}

	private static Vector<RefactoredInstance> filterEqualFunction(Vector<RefactoredInstance> refactoredInstanceList) {
		Vector<RefactoredInstance> ret = new Vector<RefactoredInstance>();
		for (int i = 0; i < refactoredInstanceList.size(); i++) {
			Vector<MyFragment> frags = refactoredInstanceList.elementAt(i).frags;
			boolean[] flags = new boolean[frags.size()];
			for (int ii = 0; ii < frags.size(); ii++) {
				flags[ii] = true;
			}
			for (int ii = 0; ii < frags.size(); ii++) {
				for (int jj = ii + 1; jj < frags.size(); jj++) {
					MyFragment f1 = frags.elementAt(ii);
					MyFragment f2 = frags.elementAt(jj);
					int which = chooseSmaller(f1, f2);
					if (which == 1) {
						flags[ii] = false;
					}
					if (which == 2) {
						flags[jj] = false;
					}
					
				}
			}
			RefactoredInstance ins = refactoredInstanceList.elementAt(i);
			Vector<Double> simistmp = new Vector<Double>();
			Vector<MyFragment> fragtmp = new Vector<MyFragment>();
			for (int ii = 0; ii < frags.size(); ii++) {
				if (flags[ii] == true) {
					fragtmp.add(frags.elementAt(ii));
					simistmp.add(ins.simis.elementAt(ii));
				}
			}
			if (fragtmp.size() < 2) {
				System.out.println("Filter:" + (i+1));
				continue;
			}
			if (fragtmp.size() == frags.size())
				ret.addElement(refactoredInstanceList.elementAt(i));
			else {
				RefactoredInstance tmp = new RefactoredInstance(fragtmp, ins.commonMethod, simistmp);
				ret.addElement(tmp);
			}
		}
		return ret;
	}
	
	public static int chooseSmaller(MyFragment f1, MyFragment f2) {
		if (!f1.srcPath.equals(f2.srcPath))
			return 0;
		if (f1.endLine >= f2.endLine && f1.startLine <= f2.startLine) {
			return 2;
		}
		if (f1.endLine <= f2.endLine && f1.startLine >= f2.startLine) {
			return 1;
		}
		
		return 0;
	}

}

class MethodVisitor extends VoidVisitorAdapter<Void> {
	
	public HashMap<Integer, Vector<String>> methodInvoke = new HashMap<Integer, Vector<String>>();
	public HashMap<String, MethodDeclaration> methodDeclare = new HashMap<String, MethodDeclaration>();
	public HashMap<Integer, Vector<MethodCallExpr>> methodCallExpr = new HashMap<Integer, Vector<MethodCallExpr>>();
	public HashMap<Integer, MethodDeclaration> methodDeclareInLine = new HashMap<Integer, MethodDeclaration>();
	
    @Override
    public void visit(MethodDeclaration n, Void arg) {
        /* here you can access the attributes of the method.
         this method will be called for all methods in this 
         CompilationUnit, including inner class methods */
        //System.out.println(n.getName() + " " + n.getBeginLine() + ", " + n.getEndLine());
    	String name = n.getName();
    	if (name.indexOf(".") != -1) {
    		name = name.substring(name.lastIndexOf(".") + 1);
    	}
        methodDeclare.put(name, n);
        if (methodDeclareInLine.containsKey(n.getBeginLine())) {
        	System.out.println("Holy Shit! This Line has two method declaration!\n" + n.toString());
        }
        methodDeclareInLine.put(n.getBeginLine(), n);
        super.visit(n, arg);
    }
    
    @Override
    public void visit(MethodCallExpr n, Void arg) {
    	//System.out.println("Line: " + n.getBeginLine() + " " + n.getName() + " " + n.getArgs());
    	String name = n.getName();
    	if (name.indexOf(".") != -1) {
    		name = name.substring(name.lastIndexOf(".") + 1);
    	}
    	if (methodInvoke.containsKey(n.getBeginLine())) {
    		Vector<String> tmp = methodInvoke.get(n.getBeginLine());
    		tmp.add(name);
    		Vector<MethodCallExpr> tt = methodCallExpr.get(n.getBeginLine());
    		tt.add(n);
    	}
    	else {
    		Vector<String> tmp = new Vector<String>();
    		tmp.add(name);
    		methodInvoke.put(n.getBeginLine(), tmp);
    		Vector<MethodCallExpr> tt = new Vector<MethodCallExpr>();
    		tt.add(n);
    		methodCallExpr.put(n.getBeginLine(), tt);
    	}
    	super.visit(n, arg);
    }
}

class SortByVersion implements Comparator {
	public int compare(Object o1, Object o2) {
		String refactorLabel = "refactored";
		String unrefactorLabel = "unrefactored";
		
		File file1=(File) o1;
		File file2=(File) o2;
		
		String fileName1=file1.getName();
		String fileName2=file2.getName();
		
		String tmp1=fileName1.substring(fileName1.indexOf("-")+1, fileName1.lastIndexOf("."));
		int endVersion1=Integer.valueOf(tmp1);
		String tmp2=fileName2.substring(fileName2.indexOf("-")+1, fileName2.lastIndexOf("."));
		int endVersion2=Integer.valueOf(tmp2);
		
		if(endVersion1>endVersion2)
			return 1;
		else return -1;
	 }
}

class VersionMethods {
	String version;
	HashMap<String, HashMap<String, MethodDeclaration>> allMethods = new HashMap<String, HashMap<String, MethodDeclaration>>();
	HashMap<String, HashMap<Integer, MethodDeclaration>> allMethodsInLine = new HashMap<String, HashMap<Integer, MethodDeclaration>>();
	public VersionMethods(String version, HashMap<String, HashMap<String, MethodDeclaration>> allMethods, 
			HashMap<String, HashMap<Integer, MethodDeclaration>> allMethodsInLine) {
		this.version = version;
		this.allMethods = allMethods;
		this.allMethodsInLine = allMethodsInLine;
	}

}

