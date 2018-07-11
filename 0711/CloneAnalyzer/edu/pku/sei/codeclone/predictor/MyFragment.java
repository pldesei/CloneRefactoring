package edu.pku.sei.codeclone.predictor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Scanner;

import japa.parser.ast.expr.ThisExpr;
import waterloo.Util.GlobalSettings;

public class MyFragment implements Serializable{
   	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int cur_ID = 0;
    public int startLine;
    public int endLine;
    int startToken;
    int endToken;
    String srcPath;
    MyFragment predecessor;
    int ID;
    int versionID;
    String versionRepoID;
    MyCloneClass fatherCloneClass;
    
    public MyFragment(int startline, int endline, int startToken, int endToken, String path, int versionID, String versionRepoID){
        this.srcPath = path;
        this.startLine = startline;
        this.endLine = endline;
        this.startToken = startToken;
        this.endToken = endToken;
        this.versionID = versionID;
        this.ID = MyFragment.cur_ID++;
        this.versionRepoID = versionRepoID;
    }
    
    public MyFragment(int startline, int endline, String path){
        this.srcPath = path;
        this.startLine = startline;
        this.endLine = endline;
    }
    
    public void setPredecessor(MyFragment frag){
        this.predecessor = frag;
    }

    public void setFatherCloneClass(MyCloneClass father) {
    	this.fatherCloneClass = father;
    }
       
    public MyFragment getPredecessor() {
        return predecessor;
    }
    
    public String getVersionRepoName() {
    	//the folder name of repoFolder=the index in commitLog.txt+1
        return this.versionRepoID;
    }
    
    public String formatVersionRepoName(String repoName){
		String newRepoName=repoName;
		/*for(int i=repoName.length()+1;i<=5;i++){
			newRepoName="0"+newRepoName;
		}*/
    	return newRepoName;
    }
    
	public String getFilePath() {
		return this.srcPath;
	}
	
	public void setFilePath(String path) {
		this.srcPath = path;
	}
	
	private String formatFilePath(String formattedRepoName){
		String filePath=this.srcPath;
		String repoName=this.getVersionRepoName();
		String pathSep=GlobalSettings.pathSep;
		return filePath.replace(pathSep+repoName+pathSep, pathSep+formattedRepoName+pathSep);
	}
	
	/*public String getFilePath(){
		String formattedRepoName=this.formatVersionRepoName(this.getVersionRepoName());
		return formatFilePath(formattedRepoName);	
	}*/
	
	private String getPreVersionRepoName(){
		int now = Integer.parseInt(this.getVersionRepoName());
		String before = (now - 1) + "";
		String newName = before;
		for(int i=before.length()+1;i<=5;i++){
			newName="0"+newName;
		}
		return newName;
	}
	
	/*Another implementation of getPreFilePath
	 * public String getPreFilePath(String repoFolderPath){
		String newPreVersionName=this.getPreVersionName();
		int curVersionNameLen=this.getVersionRepoName().length();
		String removeRepoFolderPath=this.srcPath.substring(repoFolderPath.length()+curVersionNameLen+1);
		return repoFolderPath+GlobalSettings.pathSep+newPreVersionName+removeRepoFolderPath;	
	}*/
	
	public String getPreFilePath(){
	//same file in previous repository folder, repo1 is the oldest repository
		String formattedPreRepoName= this.getPreVersionRepoName();
		return this.formatFilePath(formattedPreRepoName);
	}
	
    public int getVersion() {
        return this.versionID;
    }

    public int getId() {
        return this.ID;
    }
    

    public int getStartLine() {
        return this.startLine;
    }

    public int getEndLine() {
        return this.endLine;
    }

    public double match(MyFragment f1) {
        int interlines = Math.min(this.endLine, f1.endLine) - Math.max(this.startLine, f1.startLine);
        int length1 = this.endLine - this.startLine;
        int length2 = f1.endLine - f1.startLine;
        return 1.0*interlines/(length1+length2);
    }
    
	public String toString() {
		String ret = "";
    	ret += "(startLine=" + this.startLine + " endLine=" + this.endLine + " srcPath=" + this.srcPath + ")\r\n";
    	Scanner input = null;
    	String content1 = "";
		try {
			input = new Scanner(new File(this.getFilePath()));
			int cnt = 0;
	    	while (input.hasNext()) {
	    		String t = input.nextLine();
	    		cnt ++;
	    		if (cnt >= this.startLine && cnt <= this.endLine) {
	    			content1 += t + "\r\n";
	    		}
	    		if (cnt > this.endLine)
	    			break;
	    		
	    	}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		input.close();
		return ret+content1;
		
	}
	
	   public String commonToString() {
	    	String ret = "";
	    	ret += "(startLine=" + this.startLine + " endLine=" + this.endLine + " srcPath=" + this.srcPath + ")\r\n";
	    	Scanner input;
	    	String content1 = "";
			try {
				input = new Scanner(new File(this.getFilePath()));
				int cnt = 0;
		    	while (input.hasNext()) {
		    		String t = input.nextLine();
		    		cnt ++;
		    		if (cnt >= this.startLine && cnt <= this.endLine) {
		    			content1 += t + "\r\n";
		    		}
		    		if (cnt > this.endLine)
		    			break;
		    	}
		    	input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return ret + content1;
	    }
    
    public boolean exactMatch(MyFragment f1) {
       return this.startLine == f1.startLine && this.endLine == f1.endLine;
    }


}
