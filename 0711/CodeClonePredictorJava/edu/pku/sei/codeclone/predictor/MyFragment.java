package edu.pku.sei.codeclone.predictor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;

public class MyFragment implements Serializable{
   	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int cur_ID = 0;
	public int startLine;
    public int endLine;
    public int startToken;
    public int endToken;
    public String srcPath;
    public MyFragment predecessor;
    public int ID;
    public int versionID;
    public String versionRepoID;
    public MyCloneClass fatherCloneClass;
    
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
    
    public String getNextFilePath() {
    	int num = Integer.parseInt(this.versionRepoID);
    	int nextnum = num + 1;
    	String next = ""+nextnum;
    	for(int i=next.length()+1;i<=5;i++){
			next="0"+next;
		}
    	//System.out.println(next);
    	String nextpath = this.srcPath.replace("/" + this.versionRepoID, "/" + next);
    	return nextpath;
    }
    
    public String getNextVersionRepoID() {
    	int num = Integer.parseInt(this.versionRepoID);
    	int nextnum = num + 1;
    	String next = ""+nextnum;
    	for(int i=next.length()+1;i<=5;i++){
			next="0"+next;
		}
    	return next;
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
    
    public String getFilePath() {
    	String newRepoName = formatVersionRepoName(versionRepoID);
    	String path = srcPath.replace(versionRepoID, newRepoName);
    	return path;
    }
    
    public String formatVersionRepoName(String repoName){
		String newRepoName=repoName;
		/*for(int i=repoName.length()+1;i<=5;i++){
			newRepoName="0"+newRepoName;
		}*/
    	return newRepoName;
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
        return 2.0*interlines/(length1+length2);
    }
    
    public boolean exactMatch(MyFragment f1) {
       return this.startLine == f1.startLine && this.endLine == f1.endLine;
    }

    public String commonToString() {
    	String ret = "";
    	ret += "(startLine=" + this.startLine + " endLine=" + this.endLine + " srcPath=" + this.srcPath + ")\n";
    	Scanner input;
    	String content1 = "";
		try {
			input = new Scanner(new File(this.srcPath));
			int cnt = 0;
	    	while (input.hasNext()) {
	    		String t = input.nextLine();
	    		cnt ++;
	    		if (cnt >= this.startLine && cnt <= this.endLine) {
	    			content1 += t + "\n";
	    		}
	    		if (cnt > this.endLine)
	    			break;
	    	}
	    	input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret + content1;
    }
    
    @Override
    public String toString() {
    	String ret = "";
    	ret += "(startLine=" + this.startLine + " endLine=" + this.endLine + " srcPath=" + this.srcPath + ")\n";
    	
        Scanner input;
    	String content1 = "";
		try {
			input = new Scanner(new File(this.srcPath));
			int cnt = 0;
	    	while (input.hasNext()) {
	    		String t = input.nextLine();
	    		cnt ++;
	    		if (cnt >= this.startLine && cnt <= this.endLine) {
	    			content1 += t + "\n";
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

}
