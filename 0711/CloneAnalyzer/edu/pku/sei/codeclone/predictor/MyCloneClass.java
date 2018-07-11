package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyCloneClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MyVersion versionBelongs = null;
    List<MyFragment> fragments = new ArrayList<MyFragment>();
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

    public void setFragments(List<MyFragment> frags) {
    	this.fragments = frags;
    }
}
