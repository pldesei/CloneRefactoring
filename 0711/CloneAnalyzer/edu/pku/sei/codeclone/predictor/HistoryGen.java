package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import waterloo.History.NewRefactorCommitLocator;
import waterloo.Util.GlobalSettings;

public class HistoryGen {
	
	public String projectPath;
	//public String newestVersionRepoPath;
	//public String parentHash;
	Vector<String> allCommits = new Vector<String>();
	
	public History getHistory(MyFragment frag, HashMap<String, Date> allReponameDateTable, 
			Vector<String> filterCommits, Vector<String> allCommits, String projectPath) {
		this.projectPath = projectPath;
		this.allCommits = allCommits;
		boolean hasPred = true;
		History his = new History(allReponameDateTable);
		String curVersion = filterCommits.get(Integer.parseInt(frag.versionRepoID) - 1);
		HistoryFrag currentFrag = new HistoryFrag(frag, curVersion);
		his.addFrag(currentFrag);
		while (hasPred) {
			HistoryFrag predHistoryFrag = getPred(currentFrag, curVersion);
			if (predHistoryFrag != null) {
				hasPred = true;
				his.addFrag(predHistoryFrag);
				currentFrag = predHistoryFrag;
				curVersion = predHistoryFrag.curVersion;
			} else {
				hasPred = false;
			}
		}
		return his;
	}
	
	private HistoryFrag getPred(HistoryFrag currentFrag, String curVersion) {
		int which = -1;
		for (int i = 0; i < allCommits.size(); i++) {
			if (allCommits.get(i).equals(curVersion)) {
				which = i;
				break;
			}
		}
		if (which == 0)
			return null;
		String parentHash = allCommits.get(which - 1);
		String currentPath = currentFrag.filePath.substring(this.projectPath.length() + "Filter/00001/".length());
		ByteArrayOutputStream out = NewRefactorCommitLocator.read(projectPath, parentHash, currentPath);
		if (out == null || out.size() == 0)
			return null;
		return new HistoryFrag(-1, "", -1, currentFrag.filePath, -1, -1, true, parentHash);
	}
	
	
	/*
	public History getHistory(MyFragment frag, String repoFolderPath, HashMap<String, Date> reponameDateTable,
			String curVersion, String projectPath, String newestVersionRepoPath) {
		this.projectPath = projectPath;
		//this.newestVersionRepoPath = newestVersionRepoPath;
		boolean hasPred = true;
		History his = new History(reponameDateTable, curVersion);
		if (curVersion == null) {
			HistoryFrag currentFrag = new HistoryFrag(frag);
			while (hasPred) {
				HistoryFrag predHistoryFrag = getPred(currentFrag, repoFolderPath);
				if (predHistoryFrag != null) {
					hasPred = true;
					his.addFrag(predHistoryFrag);
					currentFrag = predHistoryFrag;
				} else {
					hasPred = false;
				}
			}
		}
		else {
			HistoryFrag currentFrag = new HistoryFrag(frag);
			while (hasPred) {
				HistoryFrag predFrag = getPredForAllClones(currentFrag, curVersion);
				if (predFrag != null) {
					hasPred = true;
					his.addFrag(predFrag);
					currentFrag = predFrag;
					curVersion = parentHash;
				}
				else {
					hasPred = false;
				}
			}
		}
		
		return his;
	}
	*/
	
/*
	private HistoryFrag getPredForAllClones(HistoryFrag currentFrag, String curVersion) {

		Git git = null;
		Repository repo = null;
		RevWalk walk = null;
		List<String> currentValue = new Vector<String>();
		List<String> predValue = new Vector<String>();
		
		try {
			git = Git.open(new File(projectPath));
			repo = git.getRepository();
			walk = new RevWalk(repo);
			ObjectId objId;
			objId = repo.resolve(curVersion);
			RevCommit revCommit = walk.parseCommit(objId);
			if (revCommit.getParents().length > 0)
				parentHash = revCommit.getParent(0).getName();
			else
				return null;
			
			String currentPath = currentFrag.filePath.substring(this.newestVersionRepoPath.length());
			ByteArrayOutputStream out = NewRefactorCommitLocator.read(projectPath, curVersion, currentPath);
			ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
			BufferedReader reader = null;
			
			try {
				reader = new BufferedReader(new InputStreamReader(input));
				String tmp = null;
				while ((tmp = reader.readLine()) != null) {
					currentValue.add(tmp);
				}
				currentValue.add("");
				input.close();
				reader.close();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					input.close();
					reader.close();
				} catch(Exception ee) {
					ee.printStackTrace();
				}
			}
			
			out = NewRefactorCommitLocator.read(projectPath, parentHash, currentPath);
			input = new ByteArrayInputStream(out.toByteArray());
			try {
				reader = new BufferedReader(new InputStreamReader(input));
				String tmp = null;
				while ((tmp = reader.readLine()) != null) {
					predValue.add(tmp);
				}
				predValue.add("");
				input.close();
				reader.close();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					input.close();
					reader.close();
				} catch(Exception ee) {
					ee.printStackTrace();
				}
			}
			
			
			Patch p = DiffUtils.diff(predValue, currentValue);
			List<Delta> deltas = p.getDeltas();
			boolean fileChanged = deltas.size() > 0;
			int currentStart = currentFrag.start;
			int currentEnd = currentFrag.end;

			if (currentStart == -1 || currentEnd == -1) {
				return new HistoryFrag(-1, "", -1, currentFrag.filePath, -1, -1, fileChanged);
			}

			for (int i = 0; i < deltas.size(); i++) {
				for (int j = i + 1; j < deltas.size(); j++) {
					if (deltas.get(i).getOriginal().getPosition() < deltas.get(j).getOriginal().getPosition()) {
						Delta d = deltas.get(i);
						deltas.set(i, deltas.get(j));
						deltas.set(j, d);
					}
				}
			}
			int[] linemap = new int[predValue.size()];
			int index1 = 0;
			int index2 = 0;

			for (int k = deltas.size() - 1; k >= 0; k--) {
				Delta del = deltas.get(k);
				int lineNumber = del.getOriginal().getPosition();
				int linesOld = del.getOriginal().getLines().size();
				int linesNew = del.getRevised().getLines().size();
				for (int i = index1; i < lineNumber; i++) {
					linemap[i] = index2;
					index2++;
				}
				index1 = lineNumber;
				if (del.getType().equals(Delta.TYPE.INSERT)) {
					index2 += linesNew;
				} else if (del.getType().equals(Delta.TYPE.DELETE)) {
					for (int i = index1; i < index1 + linesOld; i++) {
						linemap[i] = -1;
					}
					index1 += linesOld;
				} else if (del.getType().equals(Delta.TYPE.CHANGE)) {
					for (int i = index1; i < index1 + linesOld; i++) {
						linemap[i] = -1;
					}
					index1 += linesOld;
					index2 += linesNew;
				}

			}
			for (int i = index1; i < predValue.size(); i++) {
				linemap[i] = index2;
				index2++;
			}

			int predStart = -1;
			int predEnd = -1;
			for (int i = 0; i < linemap.length; i++) {
				if (currentStart <= linemap[i]) {
					predStart = i;
					break;
				}
			}
			HistoryFrag predFrag = null;
			if (predStart == -1) {
				predFrag = new HistoryFrag(-1, "", -1, currentFrag.filePath, -1, -1, fileChanged);
			} else {
				for (int i = predStart; i < linemap.length; i++) {
					if (currentEnd <= linemap[i]) {
						predEnd = i;
						break;
					}
				}
				if (predEnd == -1) {
					predFrag = new HistoryFrag(-1, "", -1, currentFrag.filePath, -1, -1, fileChanged);
				}
			}
			int changed = -1;
			for (Delta d : deltas) {
				if (d.getOriginal().getPosition() >= predStart && d.getOriginal().getPosition() <= predEnd) {
					changed = 1;
				}
				if (d.getRevised().getPosition() >= currentStart && d.getRevised().getPosition() <= currentEnd) {
					changed = 1;
				}
			}
			predFrag = new HistoryFrag(-1, "", changed, currentFrag.filePath, predStart, predEnd,
					fileChanged);
			
			
			git.close();
			repo.close();
			walk.close();
			return predFrag;
		} catch (IOException e) {
			e.printStackTrace();
			git.close();
			repo.close();
			walk.close();
			return null;
		}
	}

*/

	//before 20180703
	/*private HistoryFrag getPred(HistoryFrag currentFrag, String repoFolderPath) {
		String currentPath = currentFrag.filePath;
		int predVersionID = getPredVersion(currentFrag.versionID);
		
		String predVersionRepoName = getPredVersionRepoName(currentFrag.getVersionRepoName());
		String curVersionRepoPath = repoFolderPath + GlobalSettings.pathSep + currentFrag.getVersionRepoName();
		String predVersionRepoPath = repoFolderPath + GlobalSettings.pathSep + predVersionRepoName;

		String predPath = predVersionRepoPath + currentPath.substring(curVersionRepoPath.length());

		File tempFile = new File(predPath);
		if (!tempFile.exists()) {
			return null;
		}
		List<String> currentValue = getSrc(currentPath);
		List<String> predValue = getSrc(predPath);
		Patch p = DiffUtils.diff(predValue, currentValue);
		List<Delta> deltas = p.getDeltas();
		boolean fileChanged = deltas.size() > 0;
		int currentStart = currentFrag.start;
		int currentEnd = currentFrag.end;

		if (currentStart == -1 || currentEnd == -1) {
			return new HistoryFrag(predVersionID, predVersionRepoName, -1, predPath, -1, -1, fileChanged);
		}

		for (int i = 0; i < deltas.size(); i++) {
			for (int j = i + 1; j < deltas.size(); j++) {
				if (deltas.get(i).getOriginal().getPosition() < deltas.get(j).getOriginal().getPosition()) {
					Delta d = deltas.get(i);
					deltas.set(i, deltas.get(j));
					deltas.set(j, d);
				}
			}
		}
		int[] linemap = new int[predValue.size()];
		int index1 = 0;
		int index2 = 0;

		for (int k = deltas.size() - 1; k >= 0; k--) {
			Delta del = deltas.get(k);
			int lineNumber = del.getOriginal().getPosition();
			int linesOld = del.getOriginal().getLines().size();
			int linesNew = del.getRevised().getLines().size();
			for (int i = index1; i < lineNumber; i++) {
				linemap[i] = index2;
				index2++;
			}
			index1 = lineNumber;
			if (del.getType().equals(Delta.TYPE.INSERT)) {
				index2 += linesNew;
			} else if (del.getType().equals(Delta.TYPE.DELETE)) {
				for (int i = index1; i < index1 + linesOld; i++) {
					linemap[i] = -1;
				}
				index1 += linesOld;
			} else if (del.getType().equals(Delta.TYPE.CHANGE)) {
				for (int i = index1; i < index1 + linesOld; i++) {
					linemap[i] = -1;
				}
				index1 += linesOld;
				index2 += linesNew;
			}

		}
		for (int i = index1; i < predValue.size(); i++) {
			linemap[i] = index2;
			index2++;
		}

		int predStart = -1;
		int predEnd = -1;
		for (int i = 0; i < linemap.length; i++) {
			if (currentStart <= linemap[i]) {
				predStart = i;
				break;
			}
		}
		HistoryFrag predFrag = null;
		if (predStart == -1) {
			predFrag = new HistoryFrag(predVersionID, predVersionRepoName, -1, predPath, -1, -1, fileChanged);
		} else {
			for (int i = predStart; i < linemap.length; i++) {
				if (currentEnd <= linemap[i]) {
					predEnd = i;
					break;
				}
			}
			if (predEnd == -1) {
				predFrag = new HistoryFrag(predVersionID, predVersionRepoName, -1, predPath, -1, -1, fileChanged);
			}
		}
		int changed = -1;
		for (Delta d : deltas) {
			if (d.getOriginal().getPosition() >= predStart && d.getOriginal().getPosition() <= predEnd) {
				changed = 1;
			}
			if (d.getRevised().getPosition() >= currentStart && d.getRevised().getPosition() <= currentEnd) {
				changed = 1;
			}
		}
		predFrag = new HistoryFrag(predVersionID, predVersionRepoName, changed, predPath, predStart, predEnd,
				fileChanged);

		return predFrag;
	}
*/
	private List<String> getSrc(String path) {

		List<String> lines = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				lines.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	private String getPredVersionRepoName(String versionRepoName) {
		//int curRepoName = Integer.parseInt(versionRepoName);
		int num = Integer.parseInt(versionRepoName);
    	int prednum = num - 1;
    	String pred = ""+prednum;
    	for(int i=pred.length()+1;i<=5;i++){
			pred="0"+pred;
		}
    	return pred;
		//return Integer.toString(curRepoName + 1);
	}

	private int getPredVersion(int versionID) {
		return versionID - 1;
	}

	private int getVersion(MyFragment oldFrag) {
		return Integer.parseInt(oldFrag.versionRepoID);
	}

	private FragmentChange makeChange(MyFragment newfrag, MyFragment oldfrag, int oldVersion, int newVersion) {
		String filePathNew = newfrag.srcPath;
		int startNew = newfrag.getStartLine();
		int endNew = newfrag.getEndLine();
		String filePathOld = oldfrag.srcPath;
		int startOld = oldfrag.getStartLine();
		int endOld = oldfrag.getEndLine();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePathNew));
			int lineCount = 0;
			String fragpart = "";
			String fragpart1 = "";
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				if (lineCount >= startNew && lineCount <= endNew) {
					fragpart += line;
					fragpart += "\n";
					fragpart1 += line.trim();
				}
				lineCount++;
			}
			in.close();
			BufferedReader in1 = new BufferedReader(new FileReader(filePathOld));
			int lineCount1 = 0;
			String fragpartOld = "";
			String fragpartOld1 = "";
			for (String line = in1.readLine(); line != null; line = in1.readLine()) {
				if (lineCount1 >= startOld && lineCount1 <= endOld) {
					fragpartOld += line;
					fragpartOld += "\n";
					fragpartOld1 += line.trim();
				}
				lineCount1++;
			}
			fragpartOld1 = fragpartOld1.replaceAll(" ", "");
			fragpartOld1 = fragpartOld1.replaceAll("\t", "");
			fragpart1 = fragpart1.replaceAll(" ", "");
			fragpart1 = fragpart1.replaceAll("\t", "");

			if (fragpartOld1.equals(fragpart1)) {
				return null;
			} else {
				return new FragmentChange(oldfrag.getId(), newfrag.getId(), fragpartOld, fragpart);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
