package edu.pku.sei.codeclone.predictor.MethodCallAnalyzer;
import java.io.File;
import java.util.Comparator;

class SortByVersion implements Comparator {
	public int compare(Object o1, Object o2) {
		String refactorLabel = "refactored";
		String unrefactorLabel = "unrefactored";
		
		File file1=(File) o1;
		File file2=(File) o2;
		
		String fileName1=file1.getName();
		String fileName2=file2.getName();
		
		String tmp1=fileName1.substring(fileName1.indexOf("-")+1, fileName1.lastIndexOf("."));
		int endVersion1=Integer.valueOf(tmp1);
		String tmp2=fileName2.substring(fileName2.indexOf("-")+1, fileName2.lastIndexOf("."));
		int endVersion2=Integer.valueOf(tmp2);
		
		if(endVersion1<endVersion2)
			return 1;
		else return -1;
	 }
}