package edu.pku.sei.codeclone.predictor.code;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JavaClass implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private List<Method> privateMethods;
	private List<Method> publicMethods;
	private List<Method> protectedMethods;

	private String fullName;
	private String filePath;
	public JavaClass(String name, String filePath){
		this.fullName = name;
		this.privateMethods = new ArrayList<Method>();
		this.publicMethods = new ArrayList<Method>();
		this.protectedMethods = new ArrayList<Method>();
		this.filePath = filePath;
	}
	public String toString(){
	    String ret = fullName + "{\n";
	    for(Method m : this.publicMethods){
	        ret += m + "\n";
	    }
	    for(Method m : this.privateMethods){
	        ret += m + "\n";
	    }
        for(Method m : this.protectedMethods){
            ret += m + "\n";
        }	    
        ret = ret + "}\n";
        return ret;
	}
	public String getFullName(){
		return this.fullName;
	}
	public List<Method> getPublicMethods(){
		return this.publicMethods;
	}
	public List<Method> getPrivateMethods(){
		return this.privateMethods;
	}
	public List<Method> getProtectedMethods(){
		return this.protectedMethods;
	}

	public String getFilePath(){
		return this.filePath;
	}
	public void extractMethods(List<JavaClass> classes) {
		try {
			CompilationUnit cu = JavaParser.parse(new File(this.filePath));
			List<TypeDeclaration> types = cu.getTypes();
			List<ImportDeclaration> imports = cu.getImports();
			if(imports == null){
				imports = new ArrayList<ImportDeclaration>();
			}
			for(TypeDeclaration type : types){
				if(ModifierSet.isPublic(type.getModifiers())){
				    if(type.getMembers()!=null){
				        for(BodyDeclaration bds : type.getMembers()){
				            if(bds instanceof MethodDeclaration){
				                MethodDeclaration md = (MethodDeclaration)bds;
				                String shortReturnType = md.getType().toString();
				                String fullType = getFullType(shortReturnType, imports, classes, this);
				                if(ModifierSet.isPublic(md.getModifiers())){
				                    this.publicMethods.add(new Method(md.getName(), fullType));
				                }else if(ModifierSet.isPrivate(md.getModifiers())){
				                    this.privateMethods.add(new Method(md.getName(), fullType));
				                }else{
				                    this.protectedMethods.add(new Method(md.getName(), fullType));
				                }
				            }
				        }
				    }
				}else{
					for(BodyDeclaration bds : type.getMembers()){
						if(bds instanceof MethodDeclaration){
							MethodDeclaration md = (MethodDeclaration)bds;
							String shortReturnType = md.getType().toString();
							String fullType = getFullType(shortReturnType, imports, classes, this);
							this.privateMethods.add(new Method(md.getName(), fullType));
						}
					}
					
				}
			}
		} catch (Error e) {
			System.err.println(this.getFullName());
		} catch (Exception e) {
            // TODO Auto-generated catch block
            System.err.println(this.getFullName());
        }
	}
	public static String getFullType(String shortReturnType,
			List<ImportDeclaration> imports, List<JavaClass> classes, JavaClass currentClass) {
	    if(currentClass==null){
	        return "--library";
	    }
		if(shortReturnType.equals("int")||shortReturnType.equals("boolean")||shortReturnType.equals("float")||shortReturnType.equals("double")||shortReturnType.equals("String")||shortReturnType.equals("char")){
			return "--library";
		}
		List<String> possibleFullTypes = new ArrayList<String>();
		for(ImportDeclaration imp : imports){
			String str = imp.toString().trim();
			String impName = str.substring(7, str.length() - 1);
			if(impName.endsWith(".*")){
				possibleFullTypes.add(impName.substring(0, impName.length()-2)+shortReturnType);
			}else{
				if(impName.endsWith("."+shortReturnType)){
	                possibleFullTypes.add(impName);					
				}
			}
		}
		for(String possibleType : possibleFullTypes){
			for(JavaClass jc:classes){
				if(jc.getFullName().equals(possibleType)){
					return possibleType;
				}
			}
		}
		for(JavaClass jc : classes){
			if(jc.getPackageName().equals(currentClass.getPackageName())){
				if(jc.fullName.substring(jc.fullName.lastIndexOf('.')+1).equals(shortReturnType)){
					return jc.fullName;
				}
			}
		}
		
		return "--library";
	}
	public String getPackageName() {
		// TODO Auto-generated method stub
		if(this.fullName.indexOf('.')==-1){
			return "";
		}else{
			return this.fullName.substring(0, this.fullName.lastIndexOf('.'));
		}
	}
}
