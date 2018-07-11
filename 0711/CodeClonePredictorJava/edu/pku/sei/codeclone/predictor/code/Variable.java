package edu.pku.sei.codeclone.predictor.code;

import java.util.List;

public class Variable {
	private String type;
	private String name;
	private boolean isResolved;
	public Variable(String type, String name){
		this.type = type;
		this.name = name;
		this.isResolved = false;
	}
	public String getType(){
		return this.type;
	}
	public String getName(){
		return this.name;
	}
	public boolean isResolved(){
		return this.isResolved;
	}
	public void resolve(List<JavaClass> classes){
		this.isResolved = true;
		this.type = "";
	}
}

