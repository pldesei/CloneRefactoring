package waterloo.History;

import java.util.ArrayList;
import java.util.List;

import edu.pku.sei.codeclone.predictor.MyFragment;

public class GroupHistoryFeature {
	public int numOfCommitAllFragsChange=0,numOfCommitNoFragChange=0;
	public int numOfCommit1FragChange=0,numOfCommit2FragsChange=0,numOfCommit3FragsChange=0;
	
	private List<MyFragment> frags;
	private ArrayList<ArrayList<Integer>> groupFileChangeRecorder;
	
	public GroupHistoryFeature(List<MyFragment> frags,ArrayList<ArrayList<Integer>> groupFileChangeRecorder){
		this.frags=frags;
		this.groupFileChangeRecorder=groupFileChangeRecorder;
		this.computeGroupHistoryFeature();
	}
	
	private void computeGroupHistoryFeature(){
		int numOfHistoryCommit=0;
		for (ArrayList<Integer> insFileChangeRecorder: this.groupFileChangeRecorder) {	
			if(insFileChangeRecorder.size()>numOfHistoryCommit)
				numOfHistoryCommit=insFileChangeRecorder.size();
		}
		for (int i = 0; i < numOfHistoryCommit; i++) {
			int numOfFragChangedInThisCommit = 0;
			for (ArrayList<Integer> insFileChangeRecorder: this.groupFileChangeRecorder) {	
				if (i<insFileChangeRecorder.size()&&insFileChangeRecorder.get(i) ==1)
					numOfFragChangedInThisCommit++;
			}
			if(numOfFragChangedInThisCommit==this.frags.size())
				this.numOfCommitAllFragsChange++;	
			switch(numOfFragChangedInThisCommit){
			case 0:
				this.numOfCommitNoFragChange++;
				break;
			case 1:
				this.numOfCommit1FragChange++;
				break;
			case 2:
				this.numOfCommit2FragsChange++;
				break;
			case 3:
				this.numOfCommit3FragsChange++;
				break;
			default:
				break;	
			}
		}	
	}
}
