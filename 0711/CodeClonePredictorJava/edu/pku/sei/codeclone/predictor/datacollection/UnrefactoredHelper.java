package edu.pku.sei.codeclone.predictor.datacollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import edu.pku.sei.codeclone.predictor.MyCloneClass;
import edu.pku.sei.codeclone.predictor.MyFragment;

public class UnrefactoredHelper {
	
	public static void mergeUnrefactored(String path, int n) throws Exception {
		File file = new File(path + "unrefactored1.txt");
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		List<MyCloneClass> ret = (List<MyCloneClass>) ois.readObject();
		System.out.println("load version 1 done");
		for (int i = 2; i <= n; i++) {
			String filePath = path + "unrefactored" + i + ".txt";
			ois = new ObjectInputStream(new FileInputStream(new File(filePath)));
			List<MyCloneClass> now = (List<MyCloneClass>) ois.readObject();
			System.out.println("load version " +  i + " done");
			HashSet<MyCloneClass> alreadyInResult = new HashSet<MyCloneClass>();
			List<MyCloneClass> newResult = new Vector<MyCloneClass>();
			for (int j = 0; j < ret.size(); j++) {
				MyCloneClass c1 = ret.get(j);
				for (int k = 0; k < now.size(); k++) {
					MyCloneClass c2 = now.get(k);
					if (alreadyInResult.contains(c2))
						continue;
					if (equalMatch(c1, c2)) {
						System.out.println("new Result = " + newResult.size());
						newResult.add(c2);
						alreadyInResult.add(c2);
						break;
					}
				}
			}
			ret = newResult;
			System.out.println("ret count = " + ret.size());
		}
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path + "unrefactored.txt")));
			oos.writeObject(ret);
			oos.close();
			
			FileOutputStream fos = new FileOutputStream(new File(path + "unrefactored-readable.txt"));
			fos.write(ret.toString().getBytes());
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean equalMatch(MyCloneClass c1, MyCloneClass c2) throws Exception {
		if (c1.fragments.size() != c2.fragments.size())
			return false;
		Comparator<MyFragment> comparator = new Comparator<MyFragment>() {
			public int compare(MyFragment f1, MyFragment f2) {
				if (!f1.srcPath.equals(f2.srcPath)) {
					return f1.srcPath.compareTo(f2.srcPath);
				}
				else {
					if (f1.startToken < f2.startToken)
						return -1;
					else {
						if (f1.startToken == f2.startToken) {
							if (f1.endToken == f2.endToken) {
								return 0;
							}
							else {
								if (f1.endToken < f2.endToken) {
									return -1;
								}
								else 
									return 1;
							}
						}
						else {
							return 1;
						}
					}
				}
			}
		};
		Collections.sort(c1.fragments, comparator);
		Collections.sort(c2.fragments, comparator);
		for (int i = 0; i < c1.fragments.size(); i++) {
			if (!equalContent(c1.fragments.get(i), c2.fragments.get(i)))
				return false;
		}
		return true;
	}

	public static boolean equalContent(MyFragment f1, MyFragment f2) throws Exception {
		File file1 = new File(f1.srcPath);
		File file2 = new File(f2.srcPath);
		if (!file1.getName().equals(file2.getName())) 
			return false;
		int len1 = f1.endToken - f1.startToken + 1;
		int len2 = f2.endToken - f2.startToken + 1;
		if (len1 != len2)
			return false;
		BufferedReader reader1 = new BufferedReader(new FileReader(new File(f1.srcPath)));
		BufferedReader reader2 = new BufferedReader(new FileReader(new File(f2.srcPath)));
		String tmp = "";
		Vector<String> content1 = new Vector<String>();
		int cnt1 = 0;
		while ((tmp = reader1.readLine()) != null) {
			cnt1 ++;
			if (cnt1 >= f1.startLine && cnt1 <= f1.endLine)
				content1.addElement(tmp);
			if (cnt1 >= f1.endLine)
				break;
		}
		int cnt2 = 0;
		Vector<String> content2 = new Vector<String>();
		while ((tmp = reader2.readLine()) != null) {
			cnt2 ++;
			if (cnt2 >= f2.startLine && cnt2 <= f2.endLine)
				content2.addElement(tmp);
			if (cnt2 >= f2.endLine)
				break;
		}
		if (content1.size() != content2.size())
			return false;
		for (int i = 0; i < content1.size(); i++) {
			if (!content1.elementAt(i).equals(content2.elementAt(i)))
				return false;
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		mergeUnrefactored("D:\\gztest2\\unrefactoredResult\\", 5);
	}
}
