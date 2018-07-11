package edu.pku.sei.codeclone.predictor.datacollection;

import edu.pku.sei.codeclone.predictor.MyCloneClass;

public class MyRefactoredClass {

	MyCloneClass pred;
	MyCloneClass next;
	String nextVersionRepoID;
	
	public MyRefactoredClass(MyCloneClass c1, MyCloneClass c2, String nextVersionRepoID) {
		this.pred = c1;
		this.next = c2;
		this.nextVersionRepoID = nextVersionRepoID;
	}
	
	public String toString() {
		String ret = "Instance #\n";
		ret += pred.toString();
		if (next != null)
			ret += next.toString();
		return ret + "\n";
	}
	
}
