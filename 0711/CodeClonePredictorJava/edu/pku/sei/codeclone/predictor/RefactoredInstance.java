package edu.pku.sei.codeclone.predictor;
import java.io.Serializable;
import java.util.Vector;
public class RefactoredInstance implements Serializable, Cloneable{
	
	private static final long serialVersionUID = 1L;
	public Vector<MyFragment> frags;
	public MyFragment commonMethod;
	Vector<Double> simis;
	
	public RefactoredInstance(Vector<MyFragment> frags, MyFragment common, Vector<Double> simis) {
		this.frags = frags;
		this.commonMethod = common;
		this.simis = simis;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public String toString() {
		String ret = "Instance #\nfrags: \n";
		for (MyFragment frag : frags) {
			ret += frag.toString() + "\n";
		}
		return ret + "commonMethod: \n" + commonMethod.commonToString() + "\n\n";
	}
}