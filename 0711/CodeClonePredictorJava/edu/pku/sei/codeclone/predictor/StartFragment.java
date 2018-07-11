package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;

import de.uni_bremen.st.rcf.model.Fragment;

public class StartFragment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	private String filePath;
	private int start;
	private int end;
	private int id;
	private int tokens;
	
	private String targetFilePath;
	private int targetStart;
	private int targetEnd;
	private int targetId;
	private int targetTokens;
	private int versionID;
	private History his;
	
	public StartFragment(Fragment frag, Fragment target, int versionID, History his){
		this.filePath = ChangeGenerator.transitPath(frag.getStart().getFile().getAbsolutePath());
		this.start = frag.getStart().getLine();
		this.end = frag.getEnd().getLine();
		this.id = frag.getId();
		this.tokens = frag.getNumTokens();
		this.setHis(his);
		
		this.targetFilePath = ChangeGenerator.transitPath(target.getStart().getFile().getAbsolutePath());
		this.targetStart = target.getStart().getLine();
		this.targetEnd = target.getEnd().getLine();
		this.targetId = target.getId();
		this.targetTokens = target.getNumTokens();
		this.versionID = versionID;

	}
	public StartFragment(MyFragment myFragment, MyFragment target, int versionID, History his) {
        this.filePath = myFragment.srcPath;
        this.start = myFragment.startLine;
        this.end = myFragment.endLine;
        this.id = myFragment.ID;
        this.setHis(his);
        
        this.targetFilePath = target.srcPath;
        this.targetStart = target.startLine;
        this.targetEnd = target.endLine;
        this.targetId = target.ID;
        this.versionID = versionID;
    }
	public String getFilePath() {
		return this.filePath;
	}
	public int getStart() {
		return this.start;
	}
	public int getEnd() {
		return this.end;
	}
	public String getTargetFilePath() {
        if(this.targetFilePath.indexOf("trunk")!=-1){
            int ind = this.targetFilePath.indexOf("trunk");
            int nex = this.targetFilePath.indexOf('/', ind);
            int nexx = this.targetFilePath.indexOf('/', nex+1);
            String ret = this.targetFilePath.substring(0, ind)+this.targetFilePath.substring(nexx+1);
            System.out.println(ret);
            return ret;
        }
	    
		return targetFilePath;
	}
	public void setTargetFilePath(String targetFilePath) {
		this.targetFilePath = targetFilePath;
	}
	public int getTargetStart() {
		return targetStart;
	}
	public void setTargetStart(int targetStart) {
		this.targetStart = targetStart;
	}
	public int getTargetEnd() {
		return targetEnd;
	}
	public void setTargetEnd(int targetEnd) {
		this.targetEnd = targetEnd;
	}
	public int getTargetId() {
		return targetId;
	}
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	public int getTargetTokens() {
		return targetTokens;
	}
	public void setTargetTokens(int targetTokens) {
		this.targetTokens = targetTokens;
	}
	public int getVersionID() {
		return versionID;
	}
	public void setVersionID(int versionID) {
		this.versionID = versionID;
	}
    public History getHis() {
        return his;
    }
    public void setHis(History his) {
        this.his = his;
    }
}
