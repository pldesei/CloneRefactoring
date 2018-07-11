package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class VersionChange implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int fragSize;
	public VersionChange(int oldVersion, int newVersion){
		this.oldVersion = oldVersion;
		this.newVersion = newVersion;
		this.fChanges = new ArrayList<FragmentChange>();
	}
	public int oldVersion;
	public int newVersion;
	public List<FragmentChange> fChanges;
	public boolean isConsistent() {
		// TODO Auto-generated method stub
		if(fChanges.size()<=1){
			return false;
		}else{
			return true;
/*			for(int i = 0; i<fChanges.size(); i++){
				FragmentChange fc1 = fChanges.get(i);
				for(int j = i+1; j<fChanges.size(); j++){
					FragmentChange fc2 = fChanges.get(j);
					if(isConsistent(fc1, fc2)){
						return true;
					}
				}
			}*/
		}
//		if(fChanges.get(0).preID==499070||fChanges.get(0).preID==606353||fChanges.get(0).preID==606669){
//			return false;
//		}else{
//			return true;
//		}
	}
	private boolean isConsistent(FragmentChange fc1, FragmentChange fc2) {
		// TODO Auto-generated method stub
		List<String> fc1Old = new ArrayList<String>();
		List<String> fc1New = new ArrayList<String>();
		List<String> fc2Old = new ArrayList<String>();
		List<String> fc2New = new ArrayList<String>();
		for(int i = 0; i<fc1.oldValue.length(); i++){
			char c = fc1.oldValue.charAt(i);
			fc1Old.add(c+"");
		}
		for(int i = 0; i<fc1.newValue.length(); i++){
			char c = fc1.newValue.charAt(i);
			fc1New.add(c+"");
		}
		for(int i = 0; i<fc2.oldValue.length(); i++){
			char c = fc2.oldValue.charAt(i);
			fc2Old.add(c+"");
		}
		for(int i = 0; i<fc2.newValue.length(); i++){
			char c = fc2.newValue.charAt(i);
			fc2New.add(c+"");
		}
		Patch patch1 = DiffUtils.diff(fc1Old, fc1New);
		Patch patch2 = DiffUtils.diff(fc2Old, fc2New);
		Patch patchClone = DiffUtils.diff(fc1Old, fc2Old);
		int[] linemap = new int[fc1Old.size()];
		int index1 = 0;
		int index2 = 0;
		List<Delta> deltas = new ArrayList<Delta>();
		deltas.addAll(patchClone.getDeltas());
		for(int i = 0; i<deltas.size(); i++){
			for(int j = i+1; j<deltas.size(); j++){
				if(deltas.get(i).getOriginal().getPosition()<deltas.get(j).getOriginal().getPosition()){
					Delta d = deltas.get(i);
					deltas.set(i, deltas.get(j));
					deltas.set(j, d);
				}
			}
		}
		
		
		for(int k = deltas.size()-1;k>=0;k--){
			Delta del = deltas.get(k);
			int lineNumber = del.getOriginal().getPosition();
			int linesOld = del.getOriginal().getLines().size();
			int linesNew = del.getRevised().getLines().size();
			for(int i = index1; i<lineNumber;i++){
				linemap[i] = index2; 
				index2++;
			}
			index1=lineNumber;
			if(del.getType().equals(Delta.TYPE.INSERT)){
				index2+=linesNew;
			}else if(del.getType().equals(Delta.TYPE.DELETE)){
				for(int i = index1; i<index1+linesOld; i++){
					linemap[i] = -1;
				}
				index1+=linesOld;
			}else if(del.getType().equals(Delta.TYPE.CHANGE)){
				for(int i = index1; i<index1+linesOld; i++){
					linemap[i] = -1;
				}
				index1+=linesOld;
				index2+=linesNew;
			}
			
		}
		for(int i = index1; i<fc1Old.size();i++){
			linemap[i] = index2;
			index2++;
		}
		for(Delta del1:patch1.getDeltas()){
			for(Delta del2:patch2.getDeltas()){
				if(isConsistent(del1, del2, linemap, fc1Old, fc2Old)){
					return true;
				}
			}
		}
		return false;
	}
	private boolean isConsistent(Delta del1, Delta del2, int[] linemap, List<String> fc1Old, List<String> fc2Old) {
		// TODO Auto-generated method stub
		if(del1.getType().equals(del2.getType())){
			if(linemap[del1.getOriginal().getPosition()]==del2.getOriginal().getPosition()){
				return true;
			}
		}
		return false;
	}
}
