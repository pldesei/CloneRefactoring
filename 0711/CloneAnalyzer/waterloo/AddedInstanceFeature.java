package waterloo;

import java.io.File;

import edu.pku.sei.codeclone.predictor.MyFragment;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import waterloo.Experiment.TestTreeEditDistance;
import waterloo.Experiment.TestVisitor;

public class AddedInstanceFeature {
	
	public MyFragment frag;

	//new added
	public boolean isTest;
	public int globalVariables;
	
	//public String projectFilterPath;
	
	AddedGroupFeature cloneGroupFeature;
	
	public AddedInstanceFeature(MyFragment frag) {
		try {
		this.frag = frag;
		//this.projectFilterPath = projectFilterPath;
		
		this.isTest = frag.getFilePath().contains("test");
		CompilationUnit cu = JavaParser.parse(new File(this.frag.getFilePath()));
		TestVisitor v = new TestVisitor(this.frag.startLine, this.frag.endLine);
		v.visit(cu, null);
		//System.out.println(v.uses);
		//System.out.println(v.declares);
		//System.out.println(v.getNotLocalVarieables());
		this.globalVariables = v.getNotLocalVarieables();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		String featureString = "";	
		//new added
		featureString += this.isTest + ",";
		featureString += this.globalVariables;
		//featureString += this.cloneGroupFeature.treeDistance;
		// Output Features
		return featureString;
	}
	
}
