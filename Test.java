import java.io.*;
import java.util.*;

public class Test {

	public static void judge(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File tmp : files) {
				judge(tmp);
			}
		}
		else {
			if (file.getName().equals("feature.arff"))
				file.delete();
		}
	}

	public static void removeArffFiles(String path) {
		File file = new File(path);
		File[] files = file.listFiles();
		for (File tmp : files) {
			judge(tmp);
		}
	}

	public static void changeFileHeader() throws Exception {
		String path = "/root/CloneRefactoring/hopelastchange/lucene/";
		String tmpPath = "/root/Projects/newestVersion/lucene/00001/lucene/";
		for (int i = 1; i <= 10; i++) {
			File file = new File(path + i);
			File[] files = file.listFiles();
			for (File tmpFile : files) {
				if (tmpFile.getName().contains("cloneInstance")) {
					String ret = "";
					BufferedReader r = new BufferedReader(new FileReader(tmpFile));
					String tmp = r.readLine();
					//System.out.println(tmp);
					String start = tmp.substring(tmp.indexOf("startLine=")+10, tmp.indexOf(" endLine="));
					String end = tmp.substring(tmp.indexOf("endLine=")+8, tmp.indexOf(" srcPath="));
					String src = tmp.substring(tmp.indexOf("srcPath=")+8, tmp.indexOf(")"));
					src = src.substring(tmpPath.length());
					//System.out.println(start + " " + end + " " + src);
					
					ret += "This clone instance is located in File: \n" + src + "\n";
					ret += "The line range of this clone instance is: " + start + "-" + end + "\n";
					ret += "The content of this clone instance is as follows:\n";
					while ((tmp = r.readLine()) != null) {
						ret += tmp + "\n";
					}
					//System.out.println(ret);
					r.close();
					BufferedWriter w = new BufferedWriter(new FileWriter(tmpFile));
					w.write(ret);
					w.close();
				} 
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String path = "/root/CloneRefactoring";
		//removeArffFiles(path);
		changeFileHeader();
	}


}