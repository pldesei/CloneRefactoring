package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import edu.pku.sei.codeclone.predictor.MyFragment;

public class RefactoredInstance implements Serializable{

	private static final long serialVersionUID = 1L;
	public Vector<MyFragment> frags;
	MyFragment commonMethod;
	public Vector<Double> simis;
	Vector<Vector<Integer>> changes;
	Vector<Vector<Integer>> groupChanges;

	public RefactoredInstance(Vector<MyFragment> frags, MyFragment common, Vector<Double> simis) {
		this.frags = frags;
		this.commonMethod = common;
		this.simis = simis;
	}

	public void setChanges(Vector<Vector<Integer>> changes, Vector<Vector<Integer>> groupChanges) {
		this.changes = changes;
		this.groupChanges = groupChanges;
	}

	public Vector<MyFragment> getFragments() {
		return this.frags;
	}

	public MyFragment getCommonMethod() {
		return this.commonMethod;
	}
	
	private ArrayList<Integer> computeConsistFeatures(Vector<Vector<Integer>> changes) {
		ArrayList<Integer> consistFeatureList = new ArrayList<Integer>();
		int max = 0;
		for (int i = 0; i < changes.size(); i++) {
			Vector<Integer> tmp = changes.get(i);
			if (tmp.size() > max)
				max = tmp.size();
		}
		int[] num = new int[max];
		for (int i = 0; i < max; i++) {
			for (Vector<Integer> tmp : changes) {
				if (tmp.size() > i)
					num[i] += tmp.get(i);
			}
		}
		int all = 0, none = 0, one = 0, two = 0, three = 0;
		for (int i = 0; i < max; i++) {
			if (num[i] == this.frags.size())
				all++;
			if (num[i] == 0)
				none++;
			if (num[i] == 1)
				one++;
			if (num[i] == 2)
				two++;
			if (num[i] == 3)
				three++;
		}
		consistFeatureList.add(all);
		consistFeatureList.add(none);
		consistFeatureList.add(one);
		consistFeatureList.add(two);
		consistFeatureList.add(three);
		return consistFeatureList;
	}

	public ArrayList<Integer> getConsistChangeFeatures() {
		return computeConsistFeatures(this.changes);
	}

	public ArrayList<Integer> getConsistGroupChangeFeatures() {
		return computeConsistFeatures(this.groupChanges);
	}
	

	public String toString() {
		String ret = "Instance #\nfrags: \n";
		for (MyFragment frag : frags) {
			ret += frag.toString() + "\n";
		}
		return ret + "commonMethod: \n" + commonMethod.commonToString() + "\n\n";
	}
}
