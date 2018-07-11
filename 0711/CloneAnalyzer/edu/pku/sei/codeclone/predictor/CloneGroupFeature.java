package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

import edu.pku.sei.codeclone.predictor.code.ClassNode;
import edu.pku.sei.codeclone.predictor.code.JavaClass;
import waterloo.Experiment.TestTreeEditDistance;

public class CloneGroupFeature implements Serializable {
	private static final long serialVersionUID = 1L;
	public List<MyFragment> frags;
	public List<String> groupMethodNames;
	public List<String> groupFiles;
	public List<JavaClass> groupClasses;
	private HashSet<ClassNode> classFamilies;

	public int numInstance;// Feature 1
	public int minLeveDisForMethodNamesOfInstances;// Feature 2
	public boolean isType3;// Feature 3
	//public boolean localOrClassFamilyFile;// Feature 4

	public double avgMinLeveDis;

	// newly added features
	public boolean sameFile;
	public boolean samePackage;
	public boolean sameMethod;
	
	//add
	public boolean isClassFamily;
	public boolean localOrSiblings;//feature 4
	public List<JavaClass> classes;
	//add end
	
	//new added
	//public double treeDistance;
	//public List<CloneInstanceFeature> cloneInsFeatureListForOneGroup;

	public CloneGroupFeature(List<MyFragment> frags, List<String> methodNames, List<String> files,
			List<JavaClass> currentClasses, HashSet<ClassNode> classFamilies, List<CloneInstanceFeature> cloneInsFeatureListForOneGroup, 
			List<JavaClass> classes) {
		// TODO Auto-generated constructor stub
		this.frags = frags;
		this.groupMethodNames = methodNames;
		this.groupFiles = files;
		this.groupClasses = currentClasses;
		this.classFamilies = classFamilies;

		numInstance = this.frags.size();
		this.minLeveDisForMethodNamesOfInstances = getTotalMinLeveDis();
		this.isType3 = type3OrNot();
		//this.localOrClassFamilyFile = localClassFamilyFileOrNot();
		
		//add
		this.classes = classes;
		this.isClassFamily = classFamilyOrNot();
		this.localOrSiblings = localOrSiblings();
		//add end
		
		this.samePackage=samePackageOrNot();
		this.sameMethod=sameMethodOrNot();
		
		if(this.samePackage&&!this.sameFile)
			System.out.println("SamePackage NotSameFile!!!"+this.groupFiles);
	
//		int insSize = this.frags.size();
//		int insPairNum = insSize * (insSize - 1) / 2;
//		this.avgMinLeveDis = (double) this.minLeveDisForMethodNamesOfInstances / insPairNum;
	     this.avgMinLeveDis=this.getGroupNorMinLeveDis();
	}

	
	public double getGroupNorMinLeveDis(){
		double groupNorMinLeveDis = 0;
		for (int i = 0; i < groupMethodNames.size() - 1; i++){
			String methodName1=groupMethodNames.get(i);
			for (int j = i + 1; j < groupMethodNames.size(); j++){
				String methodName2=groupMethodNames.get(j);
				int totalLen=methodName1.length()+methodName2.length();
				groupNorMinLeveDis += (double)getMinLeveDis(methodName1, methodName2)/totalLen;	
			}		
		}	
		return groupNorMinLeveDis/this.frags.size();
	}
	
	private int getTotalMinLeveDis() {
		int minLeveDis = 0;
		for (int i = 0; i < groupMethodNames.size() - 1; i++)
			for (int j = i + 1; j < groupMethodNames.size(); j++)
				minLeveDis += getMinLeveDis(groupMethodNames.get(i), groupMethodNames.get(j));
		return minLeveDis;
	}

	private int getMinLeveDis(String str1, String str2) {
		int n = str1.length(), m = str2.length();
		int i, j;
		char c1, c2;
		int temp;
		if (n == 0)
			return m;
		if (m == 0)
			return n;
		int[][] matrix = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++) {
			matrix[i][0] = i;
		}
		for (j = 0; j <= m; j++) {
			matrix[0][j] = j;
		}
		for (i = 1; i <= n; i++) {
			c1 = str1.charAt(i - 1);
			for (j = 1; j <= m; j++) {
				c2 = str2.charAt(j - 1);
				if (c1 == c2)
					temp = 0;
				else
					temp = 1;
				matrix[i][j] = min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1, matrix[i - 1][j - 1] + temp);
			}
		}
		return matrix[n][m];
	}

	private int min(int one, int two, int three) {
		int min = one;
		if (two < min)
			min = two;
		if (three < min)
			min = three;
		return min;
	}

	private boolean type3OrNot() {
		boolean res = false;

		return res;
	}
	
	private boolean localOrSiblings() {
		this.sameFile=localFileOrNot();
		return this.sameFile||isSiblings();
	}
	
	private boolean isSiblings() {
		for(JavaClass javaClass:this.groupClasses){
			if(javaClass==null)
				return false;
		}
		JavaClass father = this.groupClasses.get(0).getSuperClass(classes);
		if (father == null)
			return false;
		for (int i = 1; i < this.groupClasses.size(); i++) {
			JavaClass javaClass = this.groupClasses.get(i);
			if (!father.equals(javaClass.getSuperClass(classes))) 
				return false;
		}
		//if (father != null)
			//System.out.println(father.fullClassName);
		return true;
	}

	/*private boolean localClassFamilyFileOrNot() {
		this.sameFile=localFileOrNot();
		return this.sameFile||classFamilyOrNot();
	}*/

	private boolean localFileOrNot() {
		HashSet<String> fileSet = new HashSet<String>();
		for (String file : groupFiles) {
			if (fileSet.isEmpty())
				fileSet.add(file);
			else if (!fileSet.contains(file))
				return false;
		}
		return true;
	}

	private boolean classFamilyOrNot() {
		for(JavaClass javaClass:this.groupClasses){
			if(javaClass==null) {
				System.out.println("javaclass is null");
				return false;
			}
		}
		boolean allInThisClassFamily;
		for (ClassNode classFamily : classFamilies) {
			allInThisClassFamily = true;
			for (JavaClass instanceClass : this.groupClasses) {
				if (!classFamily.containClass(instanceClass))
					allInThisClassFamily = false;
			}
			if (allInThisClassFamily)
				return true;
		}
		return false;
	}
	
	public boolean samePackageOrNot() {
		HashSet<String> pkgSet = new HashSet<String>();
		for (String file : groupFiles) {
			String pkg = file.substring(0, file.lastIndexOf("."));
			if (pkgSet.isEmpty())
				pkgSet.add(pkg);
			else if (!pkgSet.contains(pkg))
				return false;
		}
		return true;
	}

	public boolean sameMethodOrNot() {
		if (!this.sameFile)
			return false;
		HashSet<String> uniqueMethodNameSet = new HashSet<String>();
		for (String methodName : groupMethodNames) {
			if (uniqueMethodNameSet.isEmpty())
				uniqueMethodNameSet.add(methodName);
			else if (!uniqueMethodNameSet.contains(methodName))
				return false;
		}
		return true;
	}
	
}
