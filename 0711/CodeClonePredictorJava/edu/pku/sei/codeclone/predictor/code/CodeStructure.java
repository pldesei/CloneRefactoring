package edu.pku.sei.codeclone.predictor.code;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ThisExpr;

import java.util.ArrayList;
import java.util.List;

public class CodeStructure {
	private List<Variable> locals;
	private List<Variable> fields; 
	private List<ImportDeclaration> imports;
	
	public int numLibCall;
	public int numCall;
	public int numLocalCall;
	
	public int numParaRef;
	public int numFieldRef;
	
	public BodyDeclaration currentMethod;
	public List<JavaClass> classes;
	private List<Method> methods;
	private JavaClass currentClass;
	
	public CodeStructure(List<JavaClass> classes, String filePath, String packageName) {
		// TODO Auto-generated constructor stub
		this.locals = new ArrayList<Variable>();
		this.fields = new ArrayList<Variable>();
		this.imports = new ArrayList<ImportDeclaration>();
		this.methods  = new ArrayList<Method>();
		this.classes = classes;
		String fullName = "";
		if(packageName.equals("$default")){
		    fullName = filePath.substring(filePath.lastIndexOf('/')+1, filePath.lastIndexOf('.'));
		}else{
		    fullName = packageName + "." + filePath.substring(filePath.lastIndexOf('/')+1, filePath.lastIndexOf('.'));
		}
		if (classes == null)
			return;
		for(JavaClass jc : this.classes){
			if(jc.getFullName().equals(fullName)){
				this.currentClass = jc;
			}
		}
		if(this.currentClass==null){
		    System.err.println(fullName);
		}
	}
	public List<Variable> getLocals(){
		return this.locals;
	}
	public List<Variable> getFields(){
		return this.fields;
	}
	public List<ImportDeclaration> getImports(){
		return this.imports;
	}
	public void handleInvoke(MethodCallExpr n) {
		// TODO Auto-generated method stub
		if(n.getScope()==null||n.getScope() instanceof ThisExpr){
			this.numLocalCall++;
			return;
		}
		String fullType = getType(n.getScope());
		if(fullType.equals("--library")){
			this.numLibCall++;
			return;
		}else if(fullType.equals(this.currentClass.getFullName())){
			this.numLocalCall++;
		}
	}
	private String getType(Expression scope){
		// TODO Auto-generated method stub
		if(scope instanceof NameExpr){
			NameExpr ne = (NameExpr)scope;
			String shortType = "";
			Variable neVar = getVar(this.locals, ne.getName());
			if(neVar!=null){
				shortType = neVar.getType();
			}else{
			    if(this.currentMethod instanceof MethodDeclaration){
			        MethodDeclaration md = (MethodDeclaration)this.currentMethod;
			        Parameter p = getPara(md.getParameters(), ne.getName());
	                if(p!=null){
	                    shortType = p.getType().toString();
	                }else{
	                    Variable fVar = getVar(this.fields, ne.getName());
	                    if(fVar!=null){
	                        shortType = fVar.getType();
	                    }else{
	                        shortType = scope.toString();
	                    }
	                }			        
			    }else if(this.currentMethod instanceof ConstructorDeclaration){
			        ConstructorDeclaration md = (ConstructorDeclaration)this.currentMethod;
                    Parameter p = getPara(md.getParameters(), ne.getName());
                    if(p!=null){
                        shortType = p.getType().toString();
                    }else{
                        Variable fVar = getVar(this.fields, ne.getName());
                        if(fVar!=null){
                            shortType = fVar.getType();
                        }else{
                            shortType = scope.toString();
                        }
                    }			        
			    }
			}
			return JavaClass.getFullType(shortType, this.imports, this.classes, this.currentClass);
		}else if(scope instanceof MethodCallExpr){
			MethodCallExpr mc = (MethodCallExpr)scope;
			if(mc.getScope()==null||mc.getScope() instanceof ThisExpr){
				Method m = getMethod(this.methods, mc.getName());
				if(m!=null){
					return JavaClass.getFullType(m.returnType(), this.imports, this.classes, this.currentClass);
				}else{
					return "--library";
				}
			}else{
				return getType(getType(mc.getScope()), mc.getName());
			}
		}else{
			return "--library";
		}
	}
	private String getType(String type, String name) {
		// TODO Auto-generated method stub
		if(type.equals("--library")){
			return "--library";
		}else{
			for(JavaClass jc:this.classes){
				if(jc.getFullName().equals(type)){
					for(Method m: jc.getPublicMethods()){
						if(m.getName().equals(name)){
							return m.returnType();
						}
					}
					if(jc.getPackageName().equals(this.currentClass.getPackageName())){
						for(Method m: jc.getProtectedMethods()){
							if(m.getName().equals(name)){
								return m.returnType();
							}
						}
					}
				}
			}
		}
		return "--library";
	}
	private Method getMethod(List<Method> methods2, String name) {
		// TODO Auto-generated method stub
		for(Method m : this.methods){
			if(m.getName().equals(name)){
				return m;
			}
		}
		return null;
	}
	public void handleVariable(NameExpr expr) {
		// TODO Auto-generated method stub
	    List<Parameter> parameters = null;
	    if(this.currentMethod instanceof MethodDeclaration){
	        parameters = ((MethodDeclaration)this.currentMethod).getParameters();
	    }else if(this.currentMethod instanceof ConstructorDeclaration){
            parameters = ((ConstructorDeclaration)this.currentMethod).getParameters();	        
	    }
	    if(getVar(this.fields, expr.getName())!=null){
            this.numFieldRef++;	        
	    }else if(parameters!=null&&getPara(parameters, expr.getName())!=null){
			this.numParaRef++;
		}else{
			
		}
	}
	private Parameter getPara(List<Parameter> parameters, String name) {
	    if(parameters == null){
	        return null;
	    }
		for(Parameter p : parameters){
			if(p.getId().getName().equals(name)){
				return p;
			}
		}
		return null;
	}
	private Variable getVar(List<Variable> vars, String name) {
		for(Variable var : vars){
			if(var.getName().equals(name)){
				return var;
			}
		}
		return null;
	}
	public List<Method> getMethods() {
		// TODO Auto-generated method stub
		return this.methods;
	}
}
