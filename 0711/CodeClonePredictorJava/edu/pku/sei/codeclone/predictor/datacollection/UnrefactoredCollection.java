package edu.pku.sei.codeclone.predictor.datacollection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Vector;

import edu.pku.sei.codeclone.predictor.ForGZTest;
import edu.pku.sei.codeclone.predictor.MyCloneClass;
import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.MyVersion;
import edu.pku.sei.codeclone.predictor.MyVersionList;

public class UnrefactoredCollection {
	
	public static String unrefactoredOutputPath = "D:\\gztest2\\unrefactored.txt";
	public static String unrefactoredReadableOutputPath = "D:\\gztest2\\unrefactored_readable.txt";
	
	public static void main (String[] args) {
		
		List<MyVersion> versions = MyVersionList.load("D:\\antFilter\\", "D:\\antFilter\\");
        
        buildConsistentCloneClass(versions);
	}

	private static void buildConsistentCloneClass(List<MyVersion> versions) {
		List<MyCloneClass> predCloneClass = versions.get(0).clones;
		for (int i = 1; i < versions.size(); i++) { 
			MyVersion curVersion = versions.get(i);
			List<MyCloneClass> addedCloneClass = new Vector<MyCloneClass>();
			for (MyCloneClass mcc : curVersion.clones) {
				MyCloneClass predClass = null;
				for (MyFragment frag : mcc.fragments) {
					if (frag.predecessor != null) {
						predClass = frag.predecessor.fatherCloneClass;
						break;
					}
				}
				if (predClass != null && predCloneClass.contains(predClass) && exactMatchCloneClass(mcc, predClass)) {
					addedCloneClass.add(mcc);
				}
			}
			predCloneClass = addedCloneClass;
		}
		PrintResult(predCloneClass);
	}
	
	private static boolean exactMatchCloneClass(MyCloneClass mcc, MyCloneClass predClass) {
		if (mcc.fragments.size() != predClass.fragments.size()) return false;
		for (MyFragment frag : mcc.fragments) {
			if (frag.predecessor == null) continue;
			if (frag.predecessor.fatherCloneClass != predClass) return false;
			String nextPath = frag.srcPath;
			String predPath = nextPath.replace("\\" + frag.versionRepoID + "\\", "\\" + predClass.versionBelongs.versionRepoID + "\\");
			int[] linemap = ForGZTest.buildLineMap(nextPath, predPath);
			if (linemap == null) return false;
			int predStart = -1, currentStart = frag.startLine;
	        int predEnd = -1, currentEnd = frag.endLine;
	        for(int i = 0; i<linemap.length; i++){
	            if(currentStart <= linemap[i]){
	                predStart = i;
	                break;
	            }
	        }
	        if(predStart == -1) {
	            return false;
	        } 
	        else {       
	            for(int i = predStart; i<linemap.length; i++) {
	                if(currentEnd<=linemap[i]){
	                    predEnd = i;
	                    break;
	                }
	            }
	            if(predEnd == -1){
	                return false;
	            }
	        }
	        if (predEnd - predStart != frag.endLine - frag.startLine) return false;
		}
		return true;
	}
	
	private static void PrintResult(List<MyCloneClass> list) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(unrefactoredOutputPath)));
			oos.writeObject(list);
			oos.close();
			
			FileOutputStream fos = new FileOutputStream(new File(unrefactoredReadableOutputPath));
			fos.write(list.toString().getBytes());
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
