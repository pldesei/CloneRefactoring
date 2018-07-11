package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import de.uni_bremen.st.rcf.model.CloneClass;
import de.uni_bremen.st.rcf.model.Fragment;
import de.uni_bremen.st.rcf.model.RCF;
import de.uni_bremen.st.rcf.model.Version;
import de.uni_bremen.st.rcf.persistence.AbstractPersistenceManager;
import de.uni_bremen.st.rcf.persistence.PersistenceManagerFactory;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class MyVersionList {
    public static Hashtable<String, int[]> lineMapTable = new Hashtable<String, int[]>();
    
    public static void main (String[] args) {
    	List<MyVersion> versions = load("/home/sonia/NewExperiment/eclipse.platform.swtFilter/", "/home/sonia/NewExperiment/eclipse.platform.swtFilter/", 1, 2000);
    	try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("/home/sonia/NewExperiment/versions/eclipse.platform.swt1-2000.txt")));
			oos.writeObject(versions);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static List<MyVersion> load(String srcPath, String resPath, int start, int end) {
        List<MyVersion> versions = new ArrayList<MyVersion>();
        File srcFolder = new File(srcPath);
        int count = 1;
        
        //List<Integer> versionNums = new ArrayList<Integer>();
        List<String> versionNums = new ArrayList<String>();
        for(String versionName:srcFolder.list()){
        	if (!new File(srcPath + versionName).isDirectory()) {
        		continue;
        	}
        	if (Integer.parseInt(versionName) < start || Integer.parseInt(versionName) > end) {
        		continue;
        	}
        	try {
        		versionNums.add(versionName);
        	} catch (Exception e) {
        		System.out.println("wrong:" + versionName);
        	}
        }
        Collections.sort(versionNums);
       // System.out.println(versionNums);
        
        for(String versionName:versionNums){
            System.out.println("loading version "+ count);
            MyVersion v = loadVersion(srcPath + versionName, resPath + versionName, count);
            //System.out.println(v.getCloneClasses().size());
            versions.add(v);
            count++;
        }
        
        for(int i = 1; i < versions.size(); i++){
            System.out.println("linkinging version "+ (i + 1) + " with "+ i);
            link(versions.get(i), versions.get(i-1));
        }
        return versions;
    }

    private static void link(MyVersion curVersion, MyVersion lastVersion) {
        for(MyCloneClass cc : curVersion.getCloneClasses()){
            for(MyFragment f : cc.getFragments()){                
                f.setPredecessor(findPred(curVersion, lastVersion, f));
            }
        }
    }

    private static MyFragment findPred(MyVersion curVersion, MyVersion lastVersion, MyFragment f) {
        MyFragment tempFrag = buildPred(f, curVersion, lastVersion);
        if(tempFrag == null) 
        	return null;
        double maxSimi = 0.0;
        MyFragment maxFrag = null;
        for(MyCloneClass cc : lastVersion.getCloneClasses()){
            for(MyFragment f1 : cc.getFragments()){
                if(tempFrag.srcPath.equals(f1.srcPath)){
                    if(tempFrag.exactMatch(f1)){
                        return f1;
                    }else{
                        double simi = tempFrag.match(f1);
                        
                        if(simi > 0.3 && simi > maxSimi){
                            maxSimi = simi;
                            maxFrag = f1;
                        }
                    }
     
                }                           
            }
        }
        return maxFrag;
    }

    private static MyFragment buildPred(MyFragment f, MyVersion curVersion, MyVersion lastVersion) {
        
        String curPath = f.srcPath;        
        String predPath = f.srcPath.replace(curVersion.basePath, lastVersion.basePath);
        
        File tempFile = new File(predPath);
        if(!tempFile.exists()){
            return null;
        }
        
        List<String> currentValue = ChangeGenerator.getSrc(curPath);
        List<String> predValue = ChangeGenerator.getSrc(predPath);
        int currentStart = f.startLine;
        int currentEnd = f.endLine;
        
        int[] linemap = MyVersionList.lineMapTable.get(curPath);
        if(linemap == null){
            Patch p = DiffUtils.diff(predValue, currentValue);
            List<Delta> deltas = p.getDeltas();
                
            for(int i = 0; i<deltas.size(); i++){
                for(int j = i+1; j<deltas.size(); j++){
                    if(deltas.get(i).getOriginal().getPosition()<deltas.get(j).getOriginal().getPosition()){
                        Delta d = deltas.get(i);
                        deltas.set(i, deltas.get(j));
                        deltas.set(j, d);
                    }
                }
            }
        
            linemap = new int[predValue.size()];
            int index1 = 0;
            int index2 = 0;
        
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
            for(int i = index1; i<predValue.size();i++){
                linemap[i] = index2;
                index2++;
            }
            MyVersionList.lineMapTable.put(curPath, linemap);
        }
        
        int predStart = -1;
        int predEnd = -1;
        for(int i = 0; i<linemap.length; i++){
            if(currentStart <= linemap[i]){
                predStart = i;
                break;
            }
        }
        if(predStart == -1){
            return null;
        }else{       
            for(int i = predStart; i<linemap.length; i++){
                if(currentEnd<=linemap[i]){
                    predEnd = i;
                    break;
                }
            }
            if(predEnd == -1){
                return null;
            }
        }
        MyFragment pred = new MyFragment(predStart, predEnd, -1, -1, predPath, lastVersion.versionID, lastVersion.versionRepoID);
        return pred;
    }
    
    private static MyVersion loadVersion(String srcPath, String resPath, int count) {
        MyVersion v = new MyVersion(srcPath, count);
        HashMap<String, MyFragment> methods = new HashMap<String, MyFragment>();
        
        try {
        	BufferedReader in = new BufferedReader(new FileReader(resPath + "/headers.file"));
        	String str = "";
        	while ((str = in.readLine()) != null) {
        		String[] opt = str.split(",");
        		String num = opt[0];
        		String path = opt[1];
        		int start = Integer.parseInt(opt[2]);
        		int end = Integer.parseInt(opt[3]);
        		MyFragment method = new MyFragment(start, end, -1, -1, path, count, v.versionRepoID);
        		methods.put(num, method);
        	}
        	//System.out.println(methods.size());
        	in.close();
        	
        	int[] fa = new int[methods.size() + 10];
        	for (int i = 0; i < methods.size(); i++)
        		  fa[i] = i;

        	in = new BufferedReader(new FileReader(resPath + "/tokensclones_index_WITH_FILTER.txt"));
        	while ((str = in.readLine()) != null) {
        		String[] opt = str.split(",");
        		fa[find(Integer.parseInt(opt[0]), fa)] = find(Integer.parseInt(opt[1]), fa);
        	}
        	in.close();
        	
        	HashMap<Integer, Vector<Integer>> groups = new HashMap<Integer, Vector<Integer>>();
        	for (int i = 0; i < methods.size(); i++) {
        		int which = find(i, fa);
        		if (groups.containsKey(which)) {
        			Vector<Integer> tmp = groups.get(which);
        			tmp.addElement(i);
        			groups.put(which, tmp);
        		}
        		else {
        			Vector<Integer> tmp = new Vector<Integer>();
        			tmp.addElement(i);
        			groups.put(which, tmp);
        		}
        	}
        	
        	for (int key : groups.keySet()) {
        		Vector<Integer> tmp = groups.get(key);
        		if (tmp.size() <= 1)
        			continue;
        		Boolean[] flag = new Boolean[tmp.size()];
        		for (int i = 0; i < tmp.size(); i++)
        			flag[i] = true;
        		for (int i = 0; i < tmp.size(); i++) {
        			MyFragment fi = methods.get(tmp.elementAt(i) + "");
        			for (int j = 0; j < tmp.size(); j++) {
        				if (i == j) continue;
        				MyFragment fj = methods.get(tmp.elementAt(j) + "");
        				if (fi.srcPath.equals(fj.srcPath)) {
        					if (fi.startLine < fj.startLine && fi.endLine > fj.endLine) {
        						flag[j] = false;
        					}
        				}
        			}
        		}
        		Vector<Integer> filter = new Vector<Integer>();
        		for (int i = 0; i < tmp.size(); i++) {
        			if (flag[i] == true) {
        				filter.add(tmp.elementAt(i));
        			}
        		}
        		if (filter.size() <= 1) {
        			continue;
        		}
        		MyCloneClass cc = new MyCloneClass(v);
        		for (int i = 0; i < filter.size(); i++) {
        			MyFragment method = methods.get(filter.elementAt(i)+"");
            		cc.addFrag(method);
        		}
        		v.addClass(cc);
        	}
        	
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        return v;
    }

    private static int find(int x, int[] fa) {
		 if (fa[x] == x) 
			 return fa[x];
		 fa[x] = find(fa[x], fa);
		 return fa[x];
	}

}
