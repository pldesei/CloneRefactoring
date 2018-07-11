package edu.pku.sei.codeclone.predictor.MethodCallAnalyzer;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

public class MethodInvocationVisitor extends ASTVisitor {
	// key is lineNum, value is the method invocations of the corresponding line
	public HashMap<Integer, Vector<MethodInvocation>> lineMethodInvoMap = new HashMap<Integer, Vector<MethodInvocation>>();

	public HashMap<String, Vector<MethodDeclaration>> methodDeclareMap = new HashMap<String, Vector<MethodDeclaration>>();
	public HashSet<MethodDeclaration> methodDeclareSet = new HashSet<MethodDeclaration>();

	public PrintStream printer;
	public MethodInvocationVisitor(PrintStream printer){
		this.printer=printer;
	}
	
	public MethodInvocationVisitor() {
		this.printer = null;
	}
	
	public static void main(String[] args) {
		MethodInvocationVisitor v = null;
		CompilationUnit cu = null;

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, "1.8");

		parser.setSource(ReadFile.fileToCharArray(new File("/home/sonia/Test.java")));
		cu = (CompilationUnit) parser.createAST(null);
		v = new MethodInvocationVisitor();
		cu.accept(v);
	}
	
	public boolean visit(MethodDeclaration node) {
		String name = node.getName().toString();
		if (methodDeclareMap.containsKey(name)) {
			Vector<MethodDeclaration> tmp = methodDeclareMap.get(name);
			tmp.add(node);
		}
		else {
			Vector<MethodDeclaration> tmp = new Vector<MethodDeclaration>();
			tmp.add(node);
			methodDeclareMap.put(name, tmp);
		}
		methodDeclareSet.add(node);
		//System.out.println(name);
		/*List<SingleVariableDeclaration> paras = node.parameters();
		for (SingleVariableDeclaration para : paras) {
			System.out.println(para.getType().toString());
		}*/
		//System.out.println(paras);
		return true;
	}

	public boolean visit(MethodInvocation node) {
		int lineNum = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
//		printer.println("Line:"+lineNum);
//		printer.println("MethodInvocation:"+node);
		if (lineMethodInvoMap.containsKey(lineNum)) {
			Vector<MethodInvocation> methodInvos = lineMethodInvoMap.get(lineNum);
			methodInvos.add(node);
		} else {
			Vector<MethodInvocation> methodInvos = new Vector<MethodInvocation>();
			methodInvos.add(node);
			lineMethodInvoMap.put(lineNum, methodInvos);		
		}
		return true;
	}
}