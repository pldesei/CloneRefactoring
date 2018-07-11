package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;

import de.uni_bremen.st.rcf.model.Fragment;

public class HistoryFrag implements Serializable{
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
    public HistoryFrag(int versionID, int changed, String filePath, int start, int end, boolean fileChanged){
        this.versionID = versionID;
        this.changed = changed;
        this.filePath = filePath;
        this.start = start;
        this.end = end;
        this.fileChanged = fileChanged;
    }
    public HistoryFrag(Fragment frag) {
        // TODO Auto-generated constructor stub
        this.versionID = frag.getVersion().getId();
        this.changed = -1;
        this.filePath = ChangeGenerator.transitPath(frag.getStart().getFile().getAbsolutePath());
        this.start = frag.getStart().getLine();
        this.end = frag.getEnd().getLine();
    }
    public HistoryFrag(MyFragment frag) {
        this.versionID = frag.versionID;
        this.changed = -1;
        this.filePath = frag.srcPath;
        this.start = frag.startLine;
        this.end = frag.endLine;
    }
}
