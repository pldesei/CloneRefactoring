package edu.pku.sei.codeclone.predictor.rcf;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import de.uni_bremen.st.rcf.model.CloneClass;
import de.uni_bremen.st.rcf.model.Fragment;
import de.uni_bremen.st.rcf.model.RCF;
import de.uni_bremen.st.rcf.model.Version;
import de.uni_bremen.st.rcf.model.Versions;

public class Main {
	public static void main(String args[]){
		String rcfFile = "E:/CodeClone/icse/icse/jabref-3-30.rcf";
		String txtFile = "E:/CodeClone/icse/icse/jabref-3-30-found.txt";
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(txtFile));	
			RCF rcf = RCFLoader.loadRCF(rcfFile);
			Versions versions = rcf.getVersions();
			Iterator<Version> it = versions.iterator();
			while(it.hasNext()){
				Version nextVersion = (Version) it.next();
				pw.println();
				pw.println("Version-"+nextVersion.getId()+":");
				List<CloneClass> classes = nextVersion.getCloneClasses();
				for(CloneClass clazz:classes){
					pw.println("CloneClass-"+clazz.getId()+":");
					List<Fragment> frags = clazz.getFragments();
					int count = 0;
					for(Fragment frag:frags){
						pw.println("Frag-"+count+":");
						pw.println("id:"+frag.getId());
						String predecessors = "";
						for(Fragment pred:frag.getPredecessors()){
							predecessors += pred.getId()+",";
						}
						pw.println("predIDs:"+predecessors);
						pw.println("Start:");
						pw.println(frag.getStart().getFile().getAbsolutePath());
						pw.println(frag.getStart().getLine());
						pw.println("End:");
						pw.println(frag.getEnd().getFile().getAbsolutePath());
						pw.println(frag.getEnd().getLine());
						count++;
					}
				}
			}
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
