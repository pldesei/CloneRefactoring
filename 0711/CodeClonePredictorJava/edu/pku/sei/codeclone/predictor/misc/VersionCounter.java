package edu.pku.sei.codeclone.predictor.misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class VersionCounter {
	public static void main(String args[]){
		
		try {
			BufferedReader in = new BufferedReader(new FileReader("E:/CodeClone/icse/icse/jabref-3-100.txt"));
			SortedSet<String> versions = new TreeSet<String>();
			for(String line = in.readLine(); line!=null; line=  in.readLine()){
				if(line.equals("Start:")){
					String nextline = in.readLine();
					int index = nextline.indexOf("jabref-2005-2009")+17;
					int index2 = nextline.indexOf('\\',index);
					versions.add(nextline.substring(index, index2));
				}
			}
//			PrintWriter pw = new PrintWriter(new FileWriter(""));
			Iterator<String> it = versions.iterator();
			PrintWriter pw = new PrintWriter(new FileWriter("E:/CodeClone/icse/icse/batch.bat"));
			while(it.hasNext()){
				String versionFileName = it.next();
				String versionNumber = versionFileName.startsWith("0")?versionFileName.substring(1):versionFileName;
				pw.println("svn update -r "+versionNumber+" E:/CodeClone/jabref-all/head/");
				pw.println("xcopy head "+versionFileName+" /e /i");
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
