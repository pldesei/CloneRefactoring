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

	public static void main(String[] args) throws Exception {
		String path = "/home/ubuntu/CloneRefactoring";
		removeArffFiles(path);
		//changeFileHeader();
	}


}
