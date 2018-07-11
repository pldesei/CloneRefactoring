package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;

public class HistoryFrag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int versionID;
	public int changed;
	public String filePath;
	public int start;
	public int end;
	boolean fileChanged;
	String versionRepoID;
	String curVersion;

	public HistoryFrag(int versionID, String versionRepoID, int changed, String filePath, int start, int end,
			boolean fileChanged, String curVersion) {
		this.versionID = versionID;
		this.versionRepoID = versionRepoID;
		this.changed = changed;
		this.filePath = filePath;
		this.start = start;
		this.end = end;
		this.fileChanged = fileChanged;
		this.curVersion = curVersion;
	}

	public HistoryFrag(MyFragment frag, String curVersion) {
		this.versionID = Integer.parseInt(frag.versionRepoID);
		this.versionRepoID = frag.versionRepoID;
		this.changed = -1;
		this.filePath = frag.srcPath;
		this.start = frag.startLine;
		this.end = frag.endLine;
		this.curVersion = curVersion;
	}

	public String getVersionRepoName() {
		// TODO Auto-generated method stub
		return this.versionRepoID;
	}

	public String toString() {
		String hisFragContent = "HisFrag:";
		hisFragContent += "versionID " + versionID + ",versionRepoName " + versionRepoID + ",changed " + changed
				+ ",filePath " + filePath + ",start " + start + ",end " + end + ",fileChanged " + fileChanged + "\n";
		return hisFragContent;

	}

	public String getFilePath() {
		return this.filePath;
	}
}
