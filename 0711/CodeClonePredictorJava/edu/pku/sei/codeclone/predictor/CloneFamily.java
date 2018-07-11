package edu.pku.sei.codeclone.predictor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CloneFamily implements Comparable<CloneFamily>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int FamilyCount = 0;
	public int id;
	public int versionNum = 0;
	public int size;
	public List<StartFragment> startFragments = new ArrayList<StartFragment>();
	public StartFeature startFeature;
	public List<FragmentChange> changes = new ArrayList<FragmentChange>();
	public List<VersionChange> vChanges = new ArrayList<VersionChange>();
	public List<VersionChange> consistentChanges = new ArrayList<VersionChange>();
	
	public boolean equals(Object o){
		if(o instanceof CloneFamily){
			CloneFamily cf = (CloneFamily)o;
			return cf.id==this.id;
		}
		return false;
	}
	
	public int compareTo(CloneFamily arg0) {
		if(arg0 instanceof CloneFamily){
			CloneFamily cf = (CloneFamily)arg0;
			if(this.vChanges.size()>cf.vChanges.size()){
				return 1;
			}else if(this.vChanges.size()==cf.vChanges.size()){
				return 0;
			}else{
				return -1;
			}
		}
		return 0;
	}

	public int getConsistent(int lastVerNum) {
		// TODO Auto-generated method stub
		int count = 0;
		for(VersionChange vc:this.vChanges){
			if(vc.isConsistent() && vc.newVersion <= lastVerNum){
				count++;
				this.consistentChanges.add(vc);
			}
		}
		return count;
	}

	public void buildFeature() {
		// TODO Auto-generated method stub
		this.startFeature = new StartFeature(this.startFragments.get(0));
	}
}
