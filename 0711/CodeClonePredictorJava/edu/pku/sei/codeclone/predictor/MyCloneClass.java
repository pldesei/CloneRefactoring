package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyCloneClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MyVersion versionBelongs = null;
    public List<MyFragment> fragments = new ArrayList<MyFragment>();
    public MyCloneClass(MyVersion v){
        this.versionBelongs = v;
    }
    
    public void addFrag(MyFragment frag){
        this.fragments.add(frag);
        frag.setFatherCloneClass(this);
    }
    public List<MyFragment> getFragments() {
        // TODO Auto-generated method stub
        return this.fragments;
    }
    
    public String toString() {
    	String ret = "<";
    	for (MyFragment frag : fragments) {
    		ret += frag.toString() + ",\n";		
    	}
    	return ret + ">\n";
    }

}
