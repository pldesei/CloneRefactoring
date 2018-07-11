package edu.pku.sei.codeclone.predictor.code;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;

public class ClassNode implements Serializable {
	private static final long serialVersionUID = 1L;
	public HashSet<ClassNode> children;
	public JavaClass curJavaClass;

	public ClassNode(JavaClass javaClass) {
		this.curJavaClass = javaClass;
		children = new HashSet<ClassNode>();
	}
	
	public boolean containClass(JavaClass javaClass){
		boolean in=this.curJavaClass.equals(javaClass);
		for(ClassNode child:children)
			in=in||child.containClass(javaClass);
		return in;
	}

	public String toString() {
		String childrenInfo = "";
		if (children.size()>0) {
			childrenInfo += "\n[";
			boolean haveReachFirst=false;
			for (ClassNode child : children) {
				if(!haveReachFirst) {
					childrenInfo+=child.toString();
					haveReachFirst=true;
				}else{
					childrenInfo += "\n"+child.toString();
				}			
			}
			childrenInfo += "]";
		}
		return this.curJavaClass.filePath + childrenInfo;
	}
	@Override
	public boolean equals(Object o){
		ClassNode classNode=(ClassNode)o;
		if(this.curJavaClass.toString().equals(classNode.curJavaClass.toString()))
			return true;
		return false;	
	}
	@Override
	public int hashCode(){
		return this.curJavaClass.toString().hashCode();
	}
}
