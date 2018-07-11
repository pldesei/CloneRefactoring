package edu.pku.sei.codeclone.predictor.datacollection;

import java.util.Vector;

import edu.pku.sei.codeclone.predictor.MyFragment;

public class RefactoredInstance {

	private static final long serialVersionUID = 1L;
	public Vector<MyFragment> frags;
	public MyFragment commonMethod;
	
	public RefactoredInstance(Vector<MyFragment> frags, MyFragment common) {
		this.frags = frags;
		this.commonMethod = common;
	}
	
	public String toString() {
		String ret = "Instance #\nfrags: \n";
		for (MyFragment frag : frags) {
			ret += frag.toString() + "\n";
		}
		return ret + "commonMethod: \n" + commonMethod.commonToString() + "\n\n";
	}
	
}
