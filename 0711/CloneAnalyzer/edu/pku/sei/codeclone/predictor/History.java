package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.Serializable;

public class History implements Serializable {
	private static final long serialVersionUID = 1L;
	//private String pathSep=File.separator;
	private int numMonthOfOneYear=12;

	//public String curVersion = null;
	
	List<HistoryFrag> frags = new ArrayList<HistoryFrag>();
	//HashMap<String, Date> reponameDateTable = new HashMap<String, Date>();
	long fileMonLength;

	HashMap<String, Date> allReponameDateTable = new HashMap<String, Date>();
	
	/*
	 * Get log command:$ git log --pretty="%H,%cd,%s">log.txt
	 */
	/*public History(HashMap<String, Date> reponameDateTable, String curVersion) {
		this.reponameDateTable= reponameDateTable;
		this.curVersion = curVersion;
	}*/
	
	public History(HashMap<String, Date> allReponameDateTable) {
		//this.reponameDateTable= reponameDateTable;
		//this.curVersion = null;
		this.allReponameDateTable = allReponameDateTable;
	}
	
	public long getFileMonLength() {
		if (frags.size() == 1) {
			this.fileMonLength = 0;
			return 0;
		}
		else {
			String curVersion = frags.get(0).curVersion;
			String originVersion = frags.get(frags.size() - 1).curVersion;
			this.fileMonLength = getMonths(originVersion, curVersion);
			return this.fileMonLength;
		}
	}

/*	public long getFileMonLength(String curVersionRepoName, String projectPath) {
		
		if (frags.size() == 0) {
			this.fileMonLength = 0;
			return 0;
		} else {
			if (curVersion == null) {
				String originVersionRepoName = this.frags.get(frags.size() - 1).getVersionRepoName();
				this.fileMonLength = getMonths(originVersionRepoName, curVersionRepoName);
				return this.fileMonLength;
			} else {
				Git git = null;
				Repository repo = null;
				RevWalk walk = null;
				try {
					git = Git.open(new File(projectPath));
					repo = git.getRepository();
					walk = new RevWalk(repo);
					ObjectId objId;
					objId = repo.resolve(curVersion);
					RevCommit revCommit = walk.parseCommit(objId);
					
					int nowtime = revCommit.getCommitTime();
					Date currentDate = new Date(nowtime);
					
					String originVersionRepoName = this.frags.get(frags.size() - 1).getVersionRepoName();
					Date originDate = this.reponameDateTable.get(originVersionRepoName);
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(originDate);
					int originYear = cal.get(Calendar.YEAR);
					int originMonth = cal.get(Calendar.MONTH);
					cal.setTime(currentDate);
					int currentYear = cal.get(Calendar.YEAR);
					int currentMonth = cal.get(Calendar.MONTH);
					
					long originMonNum = originYear * numMonthOfOneYear + originMonth;
					long currentMonNum = currentYear * numMonthOfOneYear + currentMonth;
					this.fileMonLength = currentMonNum - originMonNum;
					
					git.close();
					repo.close();
					walk.close();
					return this.fileMonLength;
					
				} catch (IOException e) {
					e.printStackTrace();
					git.close();
					repo.close();
					walk.close();
				}
				
				return 0;
			}
			
		}
	}
*/
	private long getMonths(String originVersion, String curVersion) {
		Calendar cal = Calendar.getInstance();
		//Date originDate = this.reponameDateTable.get(Integer.parseInt(originRepoName)+"");
		//Date currentDate = this.reponameDateTable.get(Integer.parseInt(currentRepoName) + "");
		Date originDate = this.allReponameDateTable.get(originVersion);
		Date currentDate = this.allReponameDateTable.get(curVersion);
		cal.setTime(originDate);
		int originYear = cal.get(Calendar.YEAR);
		int originMonth = cal.get(Calendar.MONTH);
		cal.setTime(currentDate);
		int currentYear = cal.get(Calendar.YEAR);
		int currentMonth = cal.get(Calendar.MONTH);
		/*System.out.println("Versions:" + version1 + " " + version2);
		System.out.println("Dates:" + originDate + " " + currentDate);
		System.out.println("YearMon:" + originYear + "," + originMonth + " " + currentYear + "," + currentMonth);
		*/
		long originMonNum = originYear * numMonthOfOneYear + originMonth;
		long currentMonNum = currentYear * numMonthOfOneYear + currentMonth;
		this.fileMonLength = currentMonNum - originMonNum;
		return this.fileMonLength;
	}

	public int getChange() {
		int changes = 0;
		for (HistoryFrag frag : frags) {
			if (frag.changed == 1) {
				changes++;
			}
		}
		return changes;
	}

	public int getFilesChange() {
		int changes = 0;
		for (HistoryFrag frag : frags) {
			if (frag.fileChanged) {
				changes++;
			}
		}
		return changes;
	}

	public void addFrag(HistoryFrag predHistoryFrag) {
		// TODO Auto-generated method stub
		this.frags.add(predHistoryFrag);
	}
}
