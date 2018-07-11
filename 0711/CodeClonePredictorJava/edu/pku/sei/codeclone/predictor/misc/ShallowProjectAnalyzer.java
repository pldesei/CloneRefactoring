package edu.pku.sei.codeclone.predictor.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.pku.sei.codeclone.predictor.code.JavaClass;

public class ShallowProjectAnalyzer {
	public static void main(String args[]){
		String mainDir = "C:/personal/CodeClonePrediction_TSE/data/guitar_space/sources";
		File mainFolder = new File(mainDir);
		int size = mainFolder.list().length;
		int count = 0;
		for(String versionName : mainFolder.list()){
			System.out.println("Processing "+versionName + "..." + "    " + count + "/" + size);
			count++;
			String versionPath = mainDir + "/" + versionName;
			int x = Integer.parseInt(versionName);
			List<JavaClass> classes = analyze(versionPath);
			ObjectOutputStream oos;
			try {
			    PrintWriter pw = new PrintWriter(new FileWriter(versionPath + "/proj.txt"));
			    for(JavaClass jc : classes){
			        pw.println(jc);
			    }
			    pw.close();
				oos = new ObjectOutputStream(new FileOutputStream(versionPath + "/projStructure.psj"));
				oos.writeObject(classes);
				oos.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
		
	}

	private static List<JavaClass> analyze(String versionPath) {
		// TODO Auto-generated method stub
		List<String> allJavaFile = new ArrayList<String>();
		getAllJavas(versionPath, allJavaFile);
		List<JavaClass> classes = new ArrayList<JavaClass>();
		for(String javaPath: allJavaFile){
			JavaClass jc = analyzeJava(javaPath);
			if(jc!=null){
				classes.add(jc);
			}
		}
		for(JavaClass jc : classes){
	        jc.extractMethods(classes);
		}
		return classes;
	}

	private static JavaClass analyzeJava(String javaPath) {
		// TODO Auto-generated method stub
		String className = javaPath.substring(javaPath.lastIndexOf("/")+1, javaPath.lastIndexOf("."));
		try {
			BufferedReader in = new BufferedReader(new FileReader(javaPath));
			for(String line = in.readLine(); line!=null; line = in.readLine()){
				String trimLine = line.trim();
				if(trimLine.startsWith("package ")&&trimLine.endsWith(";")){
					return new JavaClass(trimLine.substring(8,trimLine.length()-1).trim()+"."+className, javaPath);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private static void getAllJavas(String path, List<String> allJavaFile) {
		// TODO Auto-generated method stub
		File file = new File(path);
		if(!file.isDirectory()){
			if(path.endsWith(".java")){
				allJavaFile.add(path);
			}else{
				return;
			}
		}else{
			for(String fileName:file.list()){
				getAllJavas(path + "/" + fileName, allJavaFile);
			}
		}
	}
}
