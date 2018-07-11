package edu.pku.sei.codeclone.predictor;

import java.util.Iterator;
import java.util.List;

import de.uni_bremen.st.rcf.model.CloneClass;
import de.uni_bremen.st.rcf.model.Fragment;
import de.uni_bremen.st.rcf.model.RCF;
import de.uni_bremen.st.rcf.model.Version;
import de.uni_bremen.st.rcf.model.Versions;

public class RCFFilter {
	private String pattern;
	
	
	public RCFFilter(String pattern) {
		this.pattern = pattern;
	}

	public void filt(RCF rcf) {
		// TODO Auto-generated method stub
		Versions versions = rcf.getVersions();
		Iterator<Version> it = versions.iterator();
		while(it.hasNext()){
			Version v = it.next();
			for(int i = 0; i<v.getCloneClasses().size(); i++){
				CloneClass clazz = v.getCloneClasses().get(i);
				List<Fragment> frags = clazz.getFragments();
				for(int j = 0; j<frags.size(); j++){
					Fragment frag = frags.get(j);
					if(mapPattern(frag)){
						frags.remove(j);
						j--;
					}
				}
				if(frags.size()<=1){
					v.getCloneClasses().remove(i);
					i--;
				}
				for(Fragment frag : frags){
					List<Fragment> preds = frag.getPredecessors();
					for(int k = 0; k<preds.size(); k++){
						Fragment pred = preds.get(k);
						if(mapPattern(pred)){
							preds.remove(pred);
							k--;
						}
					}
				}
			}
		}		
	}

	private boolean mapPattern(Fragment frag) {
		// TODO Auto-generated method stub
		return frag.getStart().getFile().getAbsolutePath().indexOf(this.pattern)!=-1;
	}

}
