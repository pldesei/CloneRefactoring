package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;

public class FragmentChange implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int preID;
	int newID;
	String oldValue;
	String newValue;
	public FragmentChange(int preID, int newID, String oldValue, String newValue){
		this.preID = preID;
		this.newID = newID;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
}
