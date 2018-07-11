package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyVersion implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String basePath;
	public int versionID;
	public String versionRepoID;
	public List<MyCloneClass> clones = new ArrayList<MyCloneClass>();
    public MyVersion(String basePath, int versionID){
        this.basePath = basePath;
        this.versionID = versionID;
        this.versionRepoID = basePath.substring(basePath.lastIndexOf('/')+1);
    }

    public String getBasepath() {
        // TODO Auto-generated method stub
        return this.basePath;
    }
    
    public void addClass(MyCloneClass cc){
        this.clones.add(cc);
    }

    public int getId() {
        // TODO Auto-generated method stub
        return this.versionID;
    }

    public List<MyCloneClass> getCloneClasses() {
        // TODO Auto-generated method stub
        return this.clones;
    }

}
