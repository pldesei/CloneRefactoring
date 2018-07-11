package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import thirdparty.similarity.Levenshtein;

import edu.pku.sei.codeclone.predictor.code.CodeStructure;
import edu.pku.sei.codeclone.predictor.code.CodeVisitor;
import edu.pku.sei.codeclone.predictor.code.JavaClass;
import edu.pku.sei.codeclone.predictor.code.Method;
import edu.pku.sei.codeclone.predictor.code.MethodVisitor;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

public class StartFeature {
	StartFragment frag;
	Method startMethod;
	Method targetMethod;
	List<String> targetParameters;
	CompilationUnit startCu;
	List<JavaClass> classes;
    private int numCall;
    private int numFieldRef;
    private int numLibCall;
    private int numLocalCall;
    private int numParaRef;
    private int lineNum;
    private boolean isTest;
    private double fileSimi;
    private double methodSimi;
    private double maxSimi;
    private double sumSimi;
    private boolean postfix;
    private int numOtherCall;
    private boolean local;
    private double maskFileSimi;
    private History his;
    private boolean localMethod;
	
	public StartFeature(StartFragment fragment) {
		// TODO Auto-generated constructor stub
		this.frag = fragment;
		this.his = fragment.getHis();
		prepareForFeatures();
		getCodeFeatures();
		getTargetFeatures(); 
	}
	private void prepareForFeatures() {
	    System.out.println("preparing for features");
		// TODO Auto-generated method stub
	    String versionID = this.frag.getVersionID() + "";
		String versionPath = this.frag.getFilePath();
		versionPath = versionPath.substring(0, versionPath.indexOf(versionID)+versionID.length());
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(versionPath+"/projStructure.psj")));
			this.classes = (List<JavaClass>)ois.readObject();
			ois.close();
			File startFile = new File(this.frag.getFilePath());
			File targetFile = new File(this.frag.getTargetFilePath());
			this.startMethod = getMethod(startFile, this.frag.getStart(), this.frag.getEnd());
			this.targetMethod = getMethod(targetFile, this.frag.getTargetStart(), this.frag.getTargetEnd());
			
		}catch(IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private Method getMethod(File file, int start, int end) {
		try {
			CompilationUnit cu = JavaParser.parse(file);
			MethodVisitor mv = new MethodVisitor(start, end);
			mv.visit(cu, null);
			return mv.getMethod();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Error e){
		    System.err.println("getMethod1:" + file);
		    e.printStackTrace();
		}
		System.err.println("getMethod2:" + file.getAbsolutePath() + ":"+start);
		return null;
	}
	private void getTargetFeatures() {
		// TODO Auto-generated method stub
            String thisFileName = this.frag.getFilePath().substring(this.frag.getFilePath().lastIndexOf('/')+1);
            
            String thisMethodName = "";
            List<String> thisParaNames = new ArrayList<String>();
            if(this.startMethod!=null){
                thisMethodName = this.startMethod.getName();
                for(String p : this.startMethod.getParameters()){
                    thisParaNames.add(p);
                }
            }else{
                System.err.println("target" + this.frag.getFilePath());
                System.err.println("target" + this.frag.getStart() + this.frag.getEnd());
                

            }
            String targetFileName = this.frag.getTargetFilePath().substring(this.frag.getTargetFilePath().lastIndexOf('/')+1);
            Method targetMethod = this.targetMethod;
            String targetMethodName = "";
            List<String> targetParaNames = new ArrayList<String>();
            if(targetMethod!=null){
                targetMethodName  = targetMethod.getName();   
                for(String p : targetMethod.getParameters()){
                    targetParaNames.add(p);
                }
            }
            this.fileSimi = simi(thisFileName, targetFileName);
            this.methodSimi = simi(thisMethodName, targetMethodName);
            this.maxSimi = maxsimi(thisParaNames, targetParaNames);
            this.sumSimi = sumsimi(thisParaNames, targetParaNames);
            this.postfix = prefix(thisMethodName).equals(prefix(targetMethodName));
            this.local = this.frag.getFilePath().equals(this.frag.getTargetFilePath());
            this.localMethod = this.local && thisMethodName.equals(targetMethodName);
            this.maskFileSimi = this.local?0:this.fileSimi;
	}
	private String prefix(String thisMethodName) {
        // TODO Auto-generated method stub
	    if(thisMethodName.equals("")){
	        return "";
	    }
        int index = 0;
        
        for(int i = thisMethodName.length()-1; i>=0; i--){
            if(Character.isDigit(thisMethodName.charAt(i))){
                
            }else{
                index = i;
            }
        }
        return thisMethodName.substring(0, index + 1);
    }
    private double maxsimi(List<String> thisParaNames, List<String> targetParaNames) {
        // TODO Auto-generated method stub
        if(thisParaNames.size()==0&&targetParaNames.size()==0){
            return 1.0;
        }
        double maxSimi = 0.0;
        for(String str1:thisParaNames){
            for(String str2:targetParaNames){
                double s = simi(str1, str2);
                if(s>maxSimi){
                    maxSimi = s;
                }
            }
        }
        return maxSimi;
    }
    private double sumsimi(List<String> thisParaNames, List<String> targetParaNames) {
        // TODO Auto-generated method stub
        if(thisParaNames.size()==0&&targetParaNames.size()==0){
            return 1.0;
        }

        double sumSimi = 0.0;
        for(String str1:thisParaNames){
            for(String str2:targetParaNames){
                double s = simi(str1, str2);
                sumSimi += s;
            }
        }
        return sumSimi;
    }

    private double simi(String str1, String str2) {
        // TODO Auto-generated method stub
        Levenshtein l = new Levenshtein();
        return l.getSimilarity(str1, str2);
    }
    private void getCodeFeatures() {
        System.out.println("Getting Code Features");
        try {
			CompilationUnit cu = JavaParser.parse(new File(this.frag.getFilePath()));
			String packageName = "";
			if(cu.getPackage()!=null){
			    packageName = cu.getPackage().getName().toString();
			}else{
			    packageName = "$default";
			}
			CodeVisitor cv = new CodeVisitor(this.frag.getStart(), this.frag.getEnd());
			CodeStructure cs = new CodeStructure(this.classes, this.frag.getFilePath(), packageName);
			cv.visit(cu, cs);
			this.numCall = cs.numCall;
			this.numFieldRef = cs.numFieldRef;
			this.numLibCall = cs.numLibCall;
			this.numLocalCall = cs.numLocalCall;
			this.numParaRef = cs.numParaRef;
			this.numOtherCall = cs.numCall - cs.numLibCall - cs.numLocalCall;
			this.lineNum = this.frag.getEnd() - this.frag.getStart();
			this.isTest = this.frag.getFilePath().indexOf("test")!=-1;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Error e){
		    System.err.println("here:" + this.frag.getFilePath());
		    e.printStackTrace();
		}
	}
	public String toString(){
	    String featureString = "";
	    featureString += this.his.length + ",";
	    featureString += this.his.fileLength + ",";
	    featureString += this.his.getChange() + ",";
	    featureString += this.his.getFilesChange() + ",";
	    featureString += this.his.getRecentChange() + ",";
	    featureString += this.his.getRecentFileChange() + ",";
	    featureString += this.isTest + ",";
        featureString += this.lineNum + ",";
	    featureString += this.numCall + ",";
        featureString += this.numLibCall + ",";
	    featureString += this.numLocalCall + ",";
	    featureString += this.numOtherCall + ",";
	    featureString += this.numFieldRef + ",";
	    featureString += this.numParaRef + ",";
	    featureString += this.fileSimi + ",";
	    featureString += this.methodSimi + ",";
	    featureString += this.maxSimi + ",";
	    featureString += this.sumSimi + ",";
        featureString += this.maskFileSimi + ",";
        featureString += this.local + ",";
//        featureString += this.localMethod + ",";
        featureString += this.postfix ;
		return featureString;
	}
	private String getSource(StartFragment fragment) {
		// TODO Auto-generated method stub
		String filePath = fragment.getFilePath();
		int start = fragment.getStart();
		int end = fragment.getEnd();
		filePath = ChangeGenerator.transitPath(filePath);
  		String fragpart = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			int linePointer = 0;
			
			for(String line = in.readLine(); line!=null; line = in.readLine()){
				if(linePointer>=start&&linePointer<=end){
					fragpart+=line; fragpart+="\n";	
				}
				linePointer++;
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fragpart;
	}

}
