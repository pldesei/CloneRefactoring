package edu.pku.sei.codeclone.predictor.MethodCallAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class ReadFile{
	static String lineSep = System.getProperty("line.separator");
	public static char[] fileToCharArray(File file) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + lineSep);
			}
			reader.close();
			return sb.toString().toCharArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
