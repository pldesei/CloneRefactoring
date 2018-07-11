package edu.pku.sei.codeclone.predictor.datacollection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Test {
	
	public static void copyFiles(String projectPath, String outputPath)  throws Exception {
		copyFiles(projectPath, outputPath, -1, -1);
	}
	
	public static void copyFiles(String projectPath, String outputPath, int start, int end) throws Exception {
		String path = projectPath + "\\filtered.txt";
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
		w.write("D:\r\n");
		w.write("cd " + projectPath + "\r\n");
		BufferedReader input = new BufferedReader(new FileReader(new File(path)));
		int cnt = 0;
		String tmp = "";
		if (start == -1) {
			while ((tmp = input.readLine()) != null) {
				cnt ++;
			
				String version = tmp.substring(0, tmp.indexOf(","));
				String name = cnt + "";
				System.out.println(name);
				w.write("git reset " + version + " --hard\r\n");
				w.write("md " + projectPath +"Filter\\" + name + "\r\n");
				w.write("xcopy " + projectPath + " " + projectPath + "Filter\\" + name + " /e\r\n");
			
			}
		}
		else {
			while ((tmp = input.readLine()) != null) {
				cnt ++;
				if (cnt < start) {
					input.readLine();
					continue;
				}
				String version = tmp.substring(0, tmp.indexOf(","));
				String name = cnt + "";
				System.out.println(name);
				w.write("git reset " + version + " --hard\r\n");
				w.write("md " + projectPath +"Filter\\" + name + "\r\n");
				w.write("xcopy " + projectPath + " " + projectPath + "Filter\\" + name + " /e\r\n");

				if (cnt >= end)
					break;
			
			}
		}
		
		input.close();
		System.out.println(cnt);
		w.write("pause");
		w.close();
	}

	public static void removeUselessFiles(String projectPath) {
		removeUselessFiles(projectPath, -1, -1);
	}
	
	public static void removeUselessFiles(String projectPath, int start, int end) {
		String path = projectPath;
		if (start == -1) {
			File file = new File(path);
			File[] files = file.listFiles();
			for (File tmp : files) {
				if (tmp.isDirectory())
					judge(tmp);
			}
		}
		else {
			for (int i = start; i <= end; i++) {
				File file = new File(path + "\\" + i);
				File[] files = file.listFiles();
				for (File tmp : files) {
					judge(tmp);
				}
			}
		}
	}

	public static void judge(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File tmp : files) {
				judge(tmp);
			}
		}
		else {
			if (!file.getName().endsWith(".java")) {
				if (file.getName().equals("a.out") || file.getName().equals("a.xml")) {

				}
				else {
					file.delete();
				}
				
			}
		}
	}

	public static void ccfx(String projectPath) throws Exception {
		ccfx(projectPath, -1, -1);
	}
	
	public static void ccfx(String projectPath, int start, int end) throws Exception{
		
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(projectPath + "\\runccfx.bat")));
		w.write("D:\r\n");
		w.write("cd " + projectPath + "\r\n");
		if (start == -1) {
			File file = new File(projectPath);
			File[] files = file.listFiles();
			for (File tmp : files) {
				if (tmp.isDirectory()) {
					String path = tmp.getAbsolutePath();
					w.write("ccfx d java -b 30 -d " + path + "\r\n");
					w.write("ccfx p a.ccfxd > " + path + "\\a.out\r\n");
					w.write("java RegexMatches " + path + "\\a.out " + path + "\\a.xml\r\n");
				}
			}
		}
		else {
			for (int i = start; i <= end; i++) {
				File tmp = new File(projectPath + "\\" + i);
				String path = tmp.getAbsolutePath();
				w.write("ccfx d java -b 30 -d " + path + "\r\n");
				w.write("ccfx p a.ccfxd > " + path + "\\a.out\r\n");
				w.write("java RegexMatches " + path + "\\a.out " + path + "\\a.xml\r\n");
			}
		}
		w.write("pause");
		w.close();
	}

	public static void commitBat(String projectPath) throws Exception {
		String path = projectPath + "\\commit.txt";
		String outputPath = projectPath + "\\run.bat";
		File file = new File(path);
		BufferedReader input = new BufferedReader(new FileReader(file));
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputPath)));
		w.write("D:\r\n");
		w.write("cd " + projectPath + "\r\n");
		w.write("md " + projectPath + "\\tmp\r\n");
		String tmp = "";
		String version = "";
		String lastVersion = "";
		int cnt = 0;
		while ((tmp = input.readLine()) != null) {
			cnt ++;
			if (cnt == 1) {
				version = tmp.substring(0, tmp.indexOf(","));
				continue;
			}
			lastVersion = tmp.substring(0, tmp.indexOf(","));
			w.write("git diff " + version + " " + lastVersion + " --numstat >" + projectPath + "\\tmp\\" + lastVersion + ".txt\r\n");
			version = lastVersion;
		}
		w.write("pause");
		w.close();
	}

	public static void filterCommit(String projectPath) throws Exception {
		HashSet<String> ret = new HashSet<String>();
		File file = new File(projectPath + "\\tmp");
		File[] files = file.listFiles();
		for (File tmp : files) {
			Scanner input = new Scanner(tmp);
			int cnt = 0;
			while (input.hasNext()) {
				String opt = input.nextLine();
				String[] t = opt.split("\t");
				try {
					int a = Integer.parseInt(t[0]);
					int b = Integer.parseInt(t[1]);
					String c = t[2];
					if (a + b >= 100 && c.endsWith(".java")) {
						cnt ++;
					}
				} catch(Exception e) {
					System.out.println(opt);
				}
			}
			if (cnt > 0) 
				ret.add(tmp.getName().substring(0, tmp.getName().indexOf(".")));
		}
		System.out.println(ret.size());
		file = new File(projectPath + "\\commit.txt");
		BufferedReader input = new BufferedReader(new FileReader(file));
		String tmp = "";
		int cnt = 0;
		String version = "";
		String lastVersion = "";
		Vector<String> result = new Vector<String>();
		while ((tmp = input.readLine()) != null) {
			cnt ++;
			if (cnt == 1) {
				version = tmp;
				continue;
			}
			lastVersion = tmp;
			if (ret.contains(lastVersion.substring(0, lastVersion.indexOf(",")))) {
				if (!result.contains(version))
					result.addElement(version);
				if (!result.contains(lastVersion))
					result.addElement(lastVersion);
			}
			version = lastVersion;
		}
		FileWriter w = new FileWriter(new File(projectPath + "\\filtered.txt"));
		for (int i = 0; i < result.size(); i++) {
			w.write(result.elementAt(i) + "\r\n");
		}
		w.close();
	}

	public static void main(String[] args) throws Exception {
		String projectPath = "D:\\guava";
		String repoPath = projectPath + "Filter";
		//copyFiles(projectPath, "D:\\gztest2\\run.bat");
		removeUselessFiles(repoPath);
		//ccfx(repoPath);
		//commitBat(projectPath);
		//filterCommit(projectPath);
	}
}
