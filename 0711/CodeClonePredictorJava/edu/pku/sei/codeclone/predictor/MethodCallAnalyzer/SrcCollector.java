package edu.pku.sei.codeclone.predictor.MethodCallAnalyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

public class SrcCollector {
	private String lineSep = System.getProperty("line.separator");
	private String fileSep = System.getProperty("file.separator");
	private HashSet<String> srcPathSet=new HashSet<String>();
	
	public void analyzeSrcPaths(String dirPath) {
		File[] files = new File(dirPath).listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				analyzeSrcPaths(file.getAbsolutePath());
			} else if (file.getName().endsWith(".java")) {
				String srcPath = filePathToSrcPath(file);
				if (srcPath != null)
					srcPathSet.add(srcPath);
			}
		}
	}

	public String filePathToSrcPath(File file) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(ReadFile.fileToCharArray(file));
		parser.setBindingsRecovery(true);

		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, "1.8");
		parser.setUnitName(file.getName());

		CompilationUnit cu1 = (CompilationUnit) parser.createAST(null);
		PackageDeclaration packageDec = cu1.getPackage();
		String packageName = null;
		if (packageDec != null) {
			String pkgLine = packageDec.toString().trim();
			packageName = pkgLine.substring(8, pkgLine.length() - 1).trim();
		}
		String pkgFileName = null;
		if (packageName != null)
			pkgFileName = packageName.replace(".", fileSep) + fileSep + file.getName();
		else
			pkgFileName = file.getName();
		String srcPath = null;
		if (file.getAbsolutePath().contains(pkgFileName)) {
			int endIndex = file.getAbsolutePath().indexOf(pkgFileName);
			srcPath = file.getAbsolutePath().substring(0, endIndex);
		}
		return srcPath;
	}
	
	public HashSet<String> getSrcPathSet(){
		return srcPathSet;
	}

	public String getFilePath(String preferedSrcPath, IMethodBinding mBinding, PrintStream printer) {
		if (mBinding == null) {
			printer.println("mBinding is null");
			return null;
		}
		String packageName = mBinding.getDeclaringClass().getPackage().toString();
		packageName = packageName.substring(8, packageName.length()).trim();
		printer.println("PackageName:" + packageName);
		String qualifiedClassName = mBinding.getDeclaringClass().getQualifiedName();
		printer.println("QualifiedClassName:" + qualifiedClassName);
		if (qualifiedClassName == null || qualifiedClassName.equals("")) {
			return null;
		}
		String className = mBinding.getDeclaringClass().getName();
		printer.println("ClassName:" + className);
		// next lineError:java.lang.StringIndexOutOfBoundsException: String
		// index out of range: -34

		String tmpStr = qualifiedClassName.substring(packageName.length() + ".".length());
		String fileName = null, filePath = null;
		if (tmpStr.contains(".")) {
			String[] tmpStrArray = tmpStr.split("\\.");
			fileName = tmpStrArray[0];
			printer.println("InnerClass");
		} else
			fileName = tmpStr;

		filePath = srcPkgFileToAbsPath(preferedSrcPath, packageName, fileName);
		if (new File(filePath).exists()) {
			return filePath;
		}

		HashSet<String> existFilePaths = new HashSet<String>();
		for (String srcPath : srcPathSet) {
			filePath = srcPkgFileToAbsPath(srcPath, packageName, fileName);
			if (new File(filePath).exists()) {
				existFilePaths.add(filePath);
			}
		}
		if (existFilePaths.size() > 1) {
			printer.println("Error:ExistFilePaths more than 2");
		}
		return filePath;
	}

	private String srcPkgFileToAbsPath(String srcPath, String packageName, String fileName) {
		return srcPath + packageName.replace(".", fileSep) + fileSep + fileName + ".java";
	}

}
