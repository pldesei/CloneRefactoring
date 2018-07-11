package waterloo.History;

import java.util.ArrayList;

import waterloo.Util.GlobalSettings;

public class InstanceHistoryFeature {
	public int numOfCommitFileExist = 0;
	public int numFileDistinctAuthor = 0;
	public int numOfCommitFileChange = 0, numOfCommitRecentFileChange = 0;
	public int numOfCommitFileInSamePkgChange = 0, numOfCommitRecentFileInSamePkgChange = 0;
	public ArrayList<Integer> fileChangeRecorder;
	public ArrayList<Integer> fileChangeRecorderForSamePkg;
	
	public GroupHistoryFeature groupHisFeature;
	
	public InstanceHistoryFeature(int numOfCommitFileExist,int numOfFileDistinctAuthor,ArrayList<Integer> fileChangeRecorder,ArrayList<Integer> fileChangeRecorderForSamePkg){
		this.numOfCommitFileExist=numOfCommitFileExist;
		this.numFileDistinctAuthor=numOfFileDistinctAuthor;
		this.fileChangeRecorder=fileChangeRecorder;
		this.fileChangeRecorderForSamePkg=fileChangeRecorderForSamePkg;
		
		this.analyzeChangeRecorder();
	}
	
	private void analyzeChangeRecorder(){
		for (int i = 0; i < fileChangeRecorder.size(); i++) {
			numOfCommitFileChange += fileChangeRecorder.get(i);
			if (i < fileChangeRecorder.size() * GlobalSettings.recentRatio)
				numOfCommitRecentFileChange += fileChangeRecorder.get(i);
		}
		for (int i = 0; i < fileChangeRecorderForSamePkg.size(); i++) {
			numOfCommitFileInSamePkgChange += fileChangeRecorderForSamePkg.get(i);
			if (i < fileChangeRecorderForSamePkg.size() * GlobalSettings.recentRatio)
				numOfCommitRecentFileInSamePkgChange += fileChangeRecorderForSamePkg.get(i);
		}
	}
	
	public String toString() {
		String featureString = "";
		featureString += this.numOfCommitFileExist + ",";
		featureString += this.numFileDistinctAuthor + ",";
		featureString += this.numOfCommitFileChange + ",";
		featureString += this.numOfCommitRecentFileChange + ",";
		featureString += this.numOfCommitFileInSamePkgChange+ ",";
		featureString += this.numOfCommitRecentFileInSamePkgChange+ ",";
		// For clone group's consistent changes in history
		featureString += this.groupHisFeature.numOfCommitAllFragsChange+ ",";
		featureString += this.groupHisFeature.numOfCommitNoFragChange+ ",";
		featureString += this.groupHisFeature.numOfCommit1FragChange+ ",";
		featureString += this.groupHisFeature.numOfCommit2FragsChange+ ",";
		featureString += this.groupHisFeature.numOfCommit3FragsChange;
		// Output Features
		return featureString;
	}
	
	public String norToString(int totalCommitNum, int totalAuthorNum){
		String featureString = "";
		featureString += (double)this.numOfCommitFileExist/totalCommitNum + ",";
		featureString += (double)this.numFileDistinctAuthor/totalAuthorNum + ",";
		featureString += (double)this.numOfCommitFileChange/totalCommitNum  + ",";
		featureString += (double)this.numOfCommitRecentFileChange/totalCommitNum  + ",";
		featureString += (double)this.numOfCommitFileInSamePkgChange/totalCommitNum + ",";
		featureString += (double)this.numOfCommitRecentFileInSamePkgChange/totalCommitNum + ",";
		// For clone group's consistent changes in history
		featureString += (double)this.groupHisFeature.numOfCommitAllFragsChange/totalCommitNum + ",";
		featureString += (double)this.groupHisFeature.numOfCommitNoFragChange/totalCommitNum + ",";
		featureString += (double)this.groupHisFeature.numOfCommit1FragChange/totalCommitNum + ",";
		featureString += (double)this.groupHisFeature.numOfCommit2FragsChange/totalCommitNum + ",";
		featureString += (double)this.groupHisFeature.numOfCommit3FragsChange/totalCommitNum ;
		// Output Features
		return featureString;
	}
}
