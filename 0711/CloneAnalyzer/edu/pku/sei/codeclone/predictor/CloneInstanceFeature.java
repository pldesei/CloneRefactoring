package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Repository;

import thirdparty.similarity.Levenshtein;
import waterloo.Experiment.TestTreeEditDistance;
import waterloo.Experiment.TestVisitor;
import waterloo.Util.TokenCounter;
import edu.pku.sei.codeclone.predictor.code.CodeFeature;
import edu.pku.sei.codeclone.predictor.code.FeatureVisitor;
import edu.pku.sei.codeclone.predictor.code.JavaClass;
import edu.pku.sei.codeclone.predictor.code.Method;
import edu.pku.sei.codeclone.predictor.code.MethodVisitor;
import japa.parser.ASTParserTokenManager;
import japa.parser.JavaCharStream;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.Token;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

public class CloneInstanceFeature implements Serializable {
	private static final long serialVersionUID = 1L;
	public RefactoredInstance fromRefactoredIns = null;
	public MyCloneClass fromUnrefactoredIns = null;

	public MyFragment frag;
	List<JavaClass> classes;
	private History his;
	// For Cloning Relationship(Feature 1-4)
	public CloneGroupFeature cloneGroupFeature;
	public Method method;
	public JavaClass currentClass;
	// For Context of Clone
	private boolean followControlStat;// Feature 5
	private long numMonthOfFile;// Feature 6
	private int numLineOfMethod;// Feature 7
	private double sizeProForFragVsMethod;// Feature 8
	// For Cloned Code Snippet
	private int numLineOfFrag;// Feature 9
	private int numTokenOfFrag;// Feature 10(ToDo)
	private boolean containCompleteControlBlock;// Feature 11
	private int cycComplexity;// Feature 12
	private double statPorForCallVsFrag;// Feature 13
	private double statPorForArithVsFrag;// Feature 14
	private boolean beginControlStat;// Feature 15

	// For test
	private int numTotalStmt;
	private int numCallStmt;
	private int numArithStmt;
	
	public String projectPath;
	public String projectFilterPath;

	public CloneInstanceFeature(MyFragment fragment, List<JavaClass> repoClasses, History his,
			RefactoredInstance refactorIns, MyCloneClass unrefactorIns, String projectPath, String projectFilterPath) {
		// System.out.println(fragment.getFilePath()+"
		// "+fragment.getStartLine()+"-"+fragment.getEndLine());
		this.frag = fragment;
		this.classes = repoClasses;
		this.his = his;
		this.fromRefactoredIns = refactorIns;
		this.fromUnrefactoredIns = unrefactorIns;
		this.projectPath = projectPath;
		this.projectFilterPath = projectFilterPath;
		prepareForFeatures();
		getCodeFeatures();
	}

	public CloneInstanceFeature(MyFragment fragment, String projectFilterPath) {
		this.frag = fragment;
		this.projectFilterPath = projectFilterPath;
		prepareForFeatures();
		getCodeFeatures();
	}

	private void prepareForFeatures() {
		//String versionID = this.frag.getVersion() + "";
		//String versionPath = this.frag.getFilePath();
		//versionPath = versionPath.substring(0, versionPath.indexOf(versionID) + versionID.length());
		try {
			File file = new File(this.frag.getFilePath());
			int start = this.frag.getStartLine(), end = this.frag.getEndLine();
			this.currentClass = getCurrentClass(this.frag.getFilePath(), start, end);
			this.method = getMethod(file, start, end);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Method getMethod(File file, int start, int end) {
		try {
			CompilationUnit cu = JavaParser.parse(file);
			MethodVisitor mv = new MethodVisitor(start, end);
			mv.visit(cu, null);
			return mv.getMethod();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private JavaClass getCurrentClass(String filePath, int start, int end) {
		JavaClass currentClass = null;
		for (JavaClass javaClass : classes) {
			// System.out.println(javaClass.filePath);
			if (javaClass.filePath.equals(filePath) && fragClassIntersect(start, end, javaClass)) {
				if (currentClass == null || hasSmallerClassRange(javaClass, currentClass))
					currentClass = javaClass;
			}
		}
		return currentClass;
	}

	private boolean fragClassIntersect(int start, int end, JavaClass javaClass) {
		if (javaClass.classStart <= start && start <= javaClass.classEnd)
			return true;
		if (javaClass.classStart <= end && end <= javaClass.classEnd)
			return true;
		return false;
	}

	private boolean hasSmallerClassRange(JavaClass classA, JavaClass classB) {
		if (classB.classStart <= classA.classStart && classA.classEnd <= classB.classEnd)
			return true;
		return false;
	}

	private void getCodeFeatures() {
		try {
			CompilationUnit cu = JavaParser.parse(new File(this.frag.getFilePath()));
			FeatureVisitor fv = new FeatureVisitor(this.frag.getStartLine(), this.frag.getEndLine());
			CodeFeature cf = new CodeFeature();
			fv.visit(cu, cf);
			// Feature 9
			this.numLineOfFrag = this.frag.getEndLine() - this.frag.getStartLine() + 1;
			// Feature 7
			if (this.method != null)
				this.numLineOfMethod = this.method.endLine - this.method.startLine + 1;
			else
				this.numLineOfMethod = 0;
			// Feature 8
			if (this.method != null)
				this.sizeProForFragVsMethod = (double) this.numLineOfFrag / this.numLineOfMethod;
			else
				this.sizeProForFragVsMethod = 0;
			// Feature 5
			this.followControlStat = cf.followControlStat;
			// Feature 11
			this.containCompleteControlBlock = cf.containCompleteControlBlock;
			// Feature 15
			this.beginControlStat = cf.beginControlStat;

			this.numTotalStmt = cf.numTotalStmt;
			this.numCallStmt = cf.numCallStmt;
			this.numArithStmt = cf.numArithStmt;
			// Feature 13
			if (this.numTotalStmt == 0)
				this.statPorForCallVsFrag = (double) 0;
			else
				this.statPorForCallVsFrag = (double) cf.numCallStmt / cf.numTotalStmt;
			// Feature 14
			if (this.numTotalStmt == 0)
				this.statPorForArithVsFrag = 0;
			else
				this.statPorForArithVsFrag = (double) cf.numArithStmt / cf.numTotalStmt;

			// Feature 6
			this.numMonthOfFile = 0;//this.his.getFileMonLength();
			// Feature 12
			this.cycComplexity = cf.cycComplexity;
			// Feature 10(CCFinder contains clone token ranges, but sourcererCC
			// doesn't
			// this.numTokenOfFrag = this.frag.endToken - this.frag.startToken +
			// 1;
			TokenCounter tokenCounter = new TokenCounter(this.frag);
			this.numTokenOfFrag = tokenCounter.getTokens();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String toStringTest(boolean addNewLoc) {
		String featureString = "";
		featureString += "Frag:" + frag.getFilePath() + "," + frag.getStartLine() + "-" + frag.getEndLine() + "\n";
		featureString += "File:" + this.frag.getFilePath() + "\n";
		if (this.his !=null && this.his.frags.size() > 0)
			featureString += "HistoryFragStartFile:" + this.his.frags.get(this.his.frags.size() - 1).getFilePath()
					+ "\n";
		else
			featureString += "NoHistory\n";
		featureString += "Class:" + this.currentClass + ",\n";
		featureString += "Method:" + this.method + ",\n";

		featureString += "NumTotalStmt:" + this.numTotalStmt + ",\n";
		featureString += "NumCallStmt:" + this.numCallStmt + ",\n";
		featureString += "NumArithStmt:" + this.numArithStmt + ",\n";

		featureString += "GroupFiles:" + this.cloneGroupFeature.groupFiles + ",\n";
		featureString += "GroupClasses:" + this.cloneGroupFeature.groupClasses + ",\n";
		featureString += "GroupMethodNames:" + this.cloneGroupFeature.groupMethodNames + ",\n";

		// For Cloning Relationship
		featureString += "1(NumInstance):" + this.cloneGroupFeature.numInstance + ",\n";
		featureString += "2(MinLeveDis):" + this.cloneGroupFeature.minLeveDisForMethodNamesOfInstances + ",\n";
		// featureString += this.cloneGroupFeature.isType3 + ","; featureString
		//featureString += "4(LocalOrClassFamily):" + this.cloneGroupFeature.localOrClassFamilyFile + ",\n";
		featureString += "4(LocalOrClassFamily):" + this.cloneGroupFeature.localOrSiblings + ",\n";

		// For Context of Clone
		featureString += "5(FollowControlStmt):" + this.followControlStat + ",\n";
		featureString += "6(NumMonthFile):" + this.numMonthOfFile + ",\n";
		featureString += "7(LineOfMethod):" + this.numLineOfMethod + ",\n";
		featureString += "8(SizePor)" + this.sizeProForFragVsMethod + ",\n";
		// For Cloned Code Snippet
		featureString += "9(LineOfFrag):" + this.numLineOfFrag + ",\n";
		featureString += "10(NumToken):" + this.numTokenOfFrag + ",\n";
		featureString += "11(containConBlock):" + this.containCompleteControlBlock + ",\n";
		featureString += "12(cycComplexity):" + this.cycComplexity + ",\n";
		featureString += "13(callStmtPor):" + this.statPorForCallVsFrag + ",\n";
		featureString += "14(arithStmtPor):" + this.statPorForArithVsFrag + ",\n";
		featureString += "15(beginConStmt):" + this.beginControlStat + ",\n";
		// New Feature
		if (addNewLoc) {
			featureString += "SameFile:" + this.cloneGroupFeature.sameFile + ",\n";
			featureString += "SamePkg:" + this.cloneGroupFeature.samePackage + ",\n";
			featureString += "SameMethod:" + this.cloneGroupFeature.sameMethod + ",\n";
		}
		// Output Features
		return featureString;
	}

	public String toString(boolean addNewLoc, boolean isTest) {
		if (isTest)
			return this.toStringTest(addNewLoc);
		String featureString = "";
		// For Cloning Relationship
		featureString += this.cloneGroupFeature.numInstance + ",";
		featureString += this.cloneGroupFeature.minLeveDisForMethodNamesOfInstances + ",";
		// featureString += this.cloneGroupFeature.isType3 + ","; featureString
		//featureString += this.cloneGroupFeature.localOrClassFamilyFile + ",";
		
		//for wangwei
		//featureString += this.cloneGroupFeature.localOrSiblings + ",";
		//for our
		featureString += this.cloneGroupFeature.isClassFamily + ",";
		
		// For Context of Clone
		featureString += this.followControlStat + ",";
		featureString += this.numMonthOfFile + ",";
		featureString += this.numLineOfMethod + ",";
		featureString += this.sizeProForFragVsMethod + ",";
		// For Cloned Code Snippet
		featureString += this.numLineOfFrag + ",";
		featureString += this.numTokenOfFrag + ",";
		featureString += this.containCompleteControlBlock + ",";
		featureString += this.cycComplexity + ",";
		featureString += this.statPorForCallVsFrag + ",";
		featureString += this.statPorForArithVsFrag + ",";
		featureString += this.beginControlStat + ",";
		// New Feature
		if (addNewLoc) {
			featureString += this.cloneGroupFeature.sameFile + ",";
			featureString += this.cloneGroupFeature.samePackage + ",";
			featureString += this.cloneGroupFeature.sameMethod + ",";
		}
		// Output Features
		return featureString;
	}

	public String normalizeToString() {
		String featureString = null;
		return featureString;

	}
}
