package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class ASTTester {
	static String lineSep = System.getProperty("line.separator");

	public static String readFileToString(File file) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + lineSep);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String path = "C:\\Users\\admin\\workspace\\APIBugRepair\\src\\test\\Apple.java";
		File file = new File(path);
		String str = readFileToString(file);

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		parser.setBindingsRecovery(true);

		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);

		String unitName = "Apple.java";
		parser.setUnitName(unitName);

		String[] sources = { "C:\\Users\\admin\\workspace\\APIBugRepair\\src\\" };
		// String[] classpath = {"C:\\Program
		// Files\\Java\\jre1.8.0_25\\lib\\rt.jar"};

		// parser.setEnvironment(classpath, sources, new String[] { "UTF-8"},
		// true);
		parser.setEnvironment(null, sources, null, true);
		parser.setSource(str.toCharArray());

		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		if (cu.getAST().hasBindingsRecovery()) {
			System.out.println("Binding activated.");
		}

		TypeFinderVisitor v = new TypeFinderVisitor();
		cu.accept(v);
	}
}

class TypeFinderVisitor extends ASTVisitor {

	public boolean visit(VariableDeclarationStatement node) {
		for (Iterator iter = node.fragments().iterator(); iter.hasNext();) {
			//System.out.println("------------------");

			VariableDeclarationFragment fragment = (VariableDeclarationFragment) iter.next();
			IVariableBinding binding = fragment.resolveBinding();

			//System.out.println("binding variable declaration: " + binding.getVariableDeclaration());
			//System.out.println("binding: " + binding);
		}
		return true;
	}

	public boolean visit(MethodInvocation node) {
		System.out.println("------------------");
		System.out.println("method invocation:" + node.toString());
		System.out.println("nodeExp:" + node.getExpression());
		if (node.getExpression() != null)
		System.out.println("nodeExpBinding:" + node.getExpression().resolveTypeBinding().getName());
		return true;
	}
}