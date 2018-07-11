package waterloo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.RefactoredInstance;

public class Filter {
	
public static void main(String[] args) throws Exception {
		
		double simi = 0.3;
		String insPath = "/home/ubuntu/result/guava";
		String featureFolderPath = "/home/ubuntu/Added/beforeFilterbugAndWrongDis/NoMonth0.1/test/guava";
		int unrefactorSize = 408;
		work(insPath, simi, featureFolderPath, unrefactorSize);
	}

	private static void outputCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(new Date()));
	}

	public static void work(String insPath, double simi, String featureFolderPath, int unrefactorSize) throws Exception {
		File cloneFileFolder = new File(insPath);
		ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
		ArrayList<File> refactorCloneFileList = new ArrayList<File>();
		for (File cloneFile : cloneFileFolder.listFiles()) {
			if(cloneFile.isDirectory())
				continue;
			String fileName = cloneFile.getName();
			if (fileName.contains("readable"))
				continue;
			if (fileName.contains("unrefactored"))
				unrefactorCloneFileList.add(cloneFile);
			else if (fileName.contains("refactored"))
				refactorCloneFileList.add(cloneFile);
		}
		Collections.sort(unrefactorCloneFileList, new SortByVersion());
		System.out.println("RefactorList:" + refactorCloneFileList);
		Collections.sort(refactorCloneFileList, new SortByVersion());
		System.out.println("UnrefactorList:" + unrefactorCloneFileList);

		Vector<RefactoredInstance> inses = new Vector<RefactoredInstance>();
		for (File cloneFile : refactorCloneFileList) {
			
			Vector<RefactoredInstance> instmp = new Vector<RefactoredInstance>();

			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
				instmp = (Vector<RefactoredInstance>) ois.readObject();
				
				System.out.print("afterReadCloneData:");
				outputCurrentTime();

				for (RefactoredInstance ins : instmp) {
					inses.addElement(ins);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Orignal: " + inses.size());

		Vector<Integer> satisfy = new Vector<Integer>();

		for (int j = 0; j < inses.size(); j++) {
			RefactoredInstance r = inses.elementAt(j);
			Vector<MyFragment> fragtmp = new Vector<MyFragment> ();
			Vector<Double> rettmp = new Vector<Double>();
			/*for (int i = 0; i < r.simis.size(); i++) {
				if (r.simis.elementAt(i) > simi) {
					fragtmp.addElement(r.frags.get(i));
					rettmp.addElement(r.simis.elementAt(i));
				}
			}*/
			String path = r.frags.get(0).getFilePath().substring(0, r.frags.get(0).getFilePath().lastIndexOf("/"));;
			boolean flag = false;
			for (int i = 0; i < r.frags.size(); i++) {
				if (r.frags.get(i).getFilePath().substring(0, r.frags.get(i).getFilePath().lastIndexOf("/")).equals(path)) {
					fragtmp.addElement(r.frags.get(i));
					rettmp.addElement(r.simis.elementAt(i));
				} else {
					flag = true;
				}
			}
			if (!flag)
				satisfy.addElement(j+1);
			/*if (rettmp.size() >= 2) {
				satisfy.addElement(j+1);
			}*/
		}

		System.out.println("After: " + satisfy.size());

		File file = new File(featureFolderPath);
		File[] files = file.listFiles();
		for (File arffFile : files) {
			if (arffFile.getName().endsWith(".arff")) {
				BufferedReader in = new BufferedReader(new FileReader(arffFile));
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(arffFile.getAbsolutePath() + "-new")));
				String t;
				boolean flag = false;
				int cnt = 0;
				int refactorcnt = 0;
				while ((t = in.readLine()) != null) {
					if (t.equals("@data")) {
						out.write(t + "\n");
						t = in.readLine();
						flag = true;
					}
					if (flag) {
						cnt ++;
						if (cnt > unrefactorSize) {
							refactorcnt ++;
							if (satisfy.contains(refactorcnt))
								out.write(t + "\n");
						}
						else {
							if (cnt <= satisfy.size() * 2)
								out.write(t + "\n");
						}
					}
					else {
						out.write(t + "\n");
					}
				}
				in.close();
				out.close();
			}
		}

	}
	

}