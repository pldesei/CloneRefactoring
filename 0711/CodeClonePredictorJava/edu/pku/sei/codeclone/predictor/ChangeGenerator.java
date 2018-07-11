package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class ChangeGenerator {
    public static void main(String args[]){
        String srcPath = "D:\\e\\";
        String resPath = "D:\\e\\";
        String txtFile = "D:\\e\\guitar-changes.txt";
        String objChangeFile = "D:\\e\\guitar-changes-object.cgs";
        String versionLogPath = "D:\\e\\commitInfo.txt";

        
        try {
    //      PrintWriter pw = new PrintWriter(new FileWriter(txtFile));
            List<MyVersion> versions = MyVersionList.load(srcPath, resPath, 0, 0);
            Hashtable<MyFragment, CloneFamily> familyTable = new Hashtable<MyFragment, CloneFamily>();
            List<CloneFamily> families = new ArrayList<CloneFamily>();
            List<String> versionlist = new ArrayList<String>();
            
            for(MyVersion nextVersion : versions){
//              if(count>200){break;}
                versionlist.add(nextVersion.getBasepath());
                System.out.println("processing version "+nextVersion.getId());
                List<MyCloneClass> classes = nextVersion.getCloneClasses();
                for(MyCloneClass clazz:classes){
                    List<MyFragment> frags = clazz.getFragments();
                    
                    List<MyFragment> fragsWithPred = new ArrayList<MyFragment>();
                    
                    for(MyFragment frag: frags){
                        if(frag.getPredecessor()!=null&&familyTable.get(frag.getPredecessor())!=null){
                            fragsWithPred.add(frag);
                        }
                    }
                    
                    if(fragsWithPred.size()>=2){
                        CloneFamily cf = determineCloneFamily(fragsWithPred, familyTable);
                        
                        if(cf!=null){
                            cf.versionNum++;
                            for(MyFragment frag: frags){
                                familyTable.put(frag, cf);
                            }
                        }
                        VersionChange vChange = null;
                        for(MyFragment frag:fragsWithPred){
                            MyFragment preFrag = frag.getPredecessor();
                            CloneFamily preCf = familyTable.get(preFrag);
                            if(preCf.equals(cf)){
                            
                                int oldVersion = getVersion(preFrag);
                                int newVersion = getVersion(frag);  
                            
                                FragmentChange fChange = makeChange(frag, preFrag, oldVersion, newVersion);
                                if(fChange!=null){
                                    if(vChange==null){
                                        vChange = new VersionChange(oldVersion, newVersion);
                                    }
                                    vChange.fChanges.add(fChange);
                                    cf.changes.add(fChange);
                                }
                            }
                        }
                        if(vChange!=null){
                            cf.vChanges.add(vChange);
                        }
                    }else{
                        long maxLength = 0;
                        History maxHis = null;
                        int maxIndex = 0;
                        for(int i = 0; i<frags.size(); i++){
                            MyFragment frag = frags.get(i);
                            History his = getHistory(frag, versionlist, versionLogPath);
                            long length = his.getLength(frag.getVersion());
                            his.getFileLength(frag.getVersion());
                            if(maxHis == null || length>maxLength){
                                maxHis = his;
                                maxLength = length;
                                maxIndex = i;
                            }
                        }
                        
                        CloneFamily cf = new CloneFamily();
                        cf.id = CloneFamily.FamilyCount++;
                        for(MyFragment frag: frags){
                            cf.size++;
                            familyTable.put(frag, cf);
                        }
                        MyFragment target = maxIndex==0?frags.get(1):frags.get(0);
                        cf.startFragments.add(new StartFragment(frags.get(maxIndex), target, getVersion(frags.get(maxIndex)), maxHis));
                        families.add(cf);
                    }
                }
            }
            Collections.sort(families);
            
            PrintWriter pw = new PrintWriter(new FileWriter(txtFile));
            for(CloneFamily cf:families){
                pw.println("***********************************");
                pw.println("Family ID:"+cf.id);
                pw.println("Number of Changes: "+cf.vChanges.size());
                pw.println("Number of Versions: "+cf.versionNum);
                for(StartFragment fr:cf.startFragments){
                    pw.println("File Path:" + fr.getFilePath());
                    pw.println("startLineNo:" + fr.getStart());
                    pw.println("endLineNo:" + fr.getEnd());
                    pw.println("familySize:" + cf.size);
                    pw.println("History:" + fr.getHis().length);
                    pw.println("History:" + fr.getHis().fileLength);
                    pw.println("History:" + fr.getHis().getChange());
                }
                for(VersionChange vc:cf.vChanges){
                    pw.println("-----------------------------------");
                    pw.println("Old version number: "+vc.oldVersion);
                    pw.println("New version number: "+vc.newVersion);
                    for(FragmentChange fc: vc.fChanges){
                        pw.println("==================");
                        pw.println("preID: "+fc.preID+" "+"newID:"+fc.newID);
                        pw.println(fc.oldValue);
                        pw.println("<<<<<<<<<<<");
                        pw.println(fc.newValue);
                    }
                }
            }
            pw.close();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(objChangeFile));
            
                oos.writeObject(families);
            oos.close();
            
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    
    private static History getHistory(MyFragment frag, List<String> versionlist, String versionLogPath) {
        // TODO Auto-generated method stub
        boolean hasPred = true;
        History his = new History(versionlist, versionLogPath);
        HistoryFrag currentFrag = new HistoryFrag(frag);
        while(hasPred){
            HistoryFrag predHistoryFrag = getPred(currentFrag, versionlist);
            if(predHistoryFrag!=null){
                hasPred = true;
                his.addFrag(predHistoryFrag);
                currentFrag = predHistoryFrag;
            }else{
                hasPred = false;
            }
        }
        return his;
    }

    private static HistoryFrag getPred(HistoryFrag currentFrag, List<String> versionlist) {
        // TODO Auto-generated method stub
        String currentPath = currentFrag.filePath;
        int predVersionID = getPredVersion(currentFrag.versionID);
        if(predVersionID == 0){return null;}
//        System.out.println(currentFrag.versionID);
        
        String predPath = versionlist.get(predVersionID - 1);
        predPath = predPath + currentPath.substring(predPath.length());
//        System.out.println(currentPath);
//        System.out.println(predPath);
        File tempFile = new File(predPath);
        if(!tempFile.exists()){
            return null;
        }
        List<String> currentValue = getSrc(currentPath);
        List<String> predValue = getSrc(predPath);
        Patch p = DiffUtils.diff(predValue, currentValue);
        List<Delta> deltas = p.getDeltas();
        boolean fileChanged = deltas.size()>0;
        int currentStart = currentFrag.start;
        int currentEnd = currentFrag.end;
        
        if(currentStart==-1||currentEnd==-1){
            return new HistoryFrag(predVersionID, -1, predPath, -1, -1, fileChanged);
        }
        
        for(int i = 0; i<deltas.size(); i++){
            for(int j = i+1; j<deltas.size(); j++){
                if(deltas.get(i).getOriginal().getPosition()<deltas.get(j).getOriginal().getPosition()){
                    Delta d = deltas.get(i);
                    deltas.set(i, deltas.get(j));
                    deltas.set(j, d);
                }
            }
        }
        int[] linemap = new int[predValue.size()];
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
        
        int predStart = -1;
        int predEnd = -1;
        for(int i = 0; i<linemap.length; i++){
            if(currentStart<=linemap[i]){
                predStart = i;
                break;
            }
        }
        HistoryFrag predFrag = null;
        if(predStart == -1){
            predFrag = new HistoryFrag(predVersionID, -1, predPath, -1, -1, fileChanged);
        }else{       
            for(int i = predStart; i<linemap.length; i++){
                if(currentEnd<=linemap[i]){
                    predEnd = i;
                    break;
                }
            }
            if(predEnd == -1){
                predFrag = new HistoryFrag(predVersionID, -1, predPath, -1, -1, fileChanged);                
            }
        }
        int changed = -1;
        for(Delta d: deltas){
            if(d.getOriginal().getPosition()>=predStart&&d.getOriginal().getPosition()<=predEnd){
                changed = 1;
            }
            if(d.getRevised().getPosition()>=currentStart&&d.getRevised().getPosition()<=currentEnd){
                changed = 1;
            }
        }
        predFrag = new HistoryFrag(predVersionID, changed, predPath, predStart, predEnd, fileChanged);
        return predFrag;
        
    }

    public static List<String> getSrc(String path) {
        // TODO Auto-generated method stub
        List<String> lines = new ArrayList<String>();
        try{
            BufferedReader in = new BufferedReader(new FileReader(path));
            for(String line = in.readLine(); line!=null; line = in.readLine()){
                lines.add(line);
            }
            lines.add("");
            in.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return lines;
    }

    private static int getPredVersion(int versionID) {
        // TODO Auto-generated method stub
        return versionID - 1;
    }

    private static CloneFamily determineCloneFamily(List<MyFragment> fragsWithPred, Hashtable<MyFragment, CloneFamily> familyTable) {
        HashSet<CloneFamily> cfs = new HashSet<CloneFamily>();
        for(MyFragment frag:fragsWithPred){
            CloneFamily preCf = familyTable.get(frag.getPredecessor());
            if(preCf!=null){
                if(cfs.contains(preCf)){
                    return preCf;
                }else{
                    cfs.add(preCf);
                }
            }
        }
        return null;
    }

    private static int getVersion(MyFragment oldFrag) {
        // TODO Auto-generated method stub
        return Integer.parseInt(oldFrag.versionRepoID);
    	//return oldFrag.versionRepoID.charAt(0)-'a' + Integer.parseInt(oldFrag.versionRepoID.charAt(1)+"");
    }

    private static FragmentChange makeChange(MyFragment newfrag, MyFragment oldfrag, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        String filePathNew = newfrag.srcPath;
        int startNew = newfrag.getStartLine();
        int endNew = newfrag.getEndLine();
        String filePathOld = oldfrag.srcPath;
        int startOld = oldfrag.getStartLine();
        int endOld = oldfrag.getEndLine();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePathNew));
            int lineCount = 0;
            String fragpart = "";
            String fragpart1 = "";
            for(String line = in.readLine(); line!=null; line = in.readLine()){
                if(lineCount>=startNew&&lineCount<=endNew){
                    fragpart+=line; fragpart+="\r\n"; 
                    fragpart1+=line.trim();
                }
                lineCount++;
            }
            in.close();
            BufferedReader in1 = new BufferedReader(new FileReader(filePathOld));
            int lineCount1 = 0;
            String fragpartOld = "";
            String fragpartOld1 = "";
            for(String line = in1.readLine(); line!=null; line = in1.readLine()){
                if(lineCount1>=startOld&&lineCount1<=endOld){
                    fragpartOld+=line; fragpartOld+="\r\n";   
                    fragpartOld1+=line.trim();
                }
                lineCount1++;
            }
            in1.close();
            fragpartOld1 = fragpartOld1.replaceAll(" ", "");
            fragpartOld1 = fragpartOld1.replaceAll("\t", "");
            fragpart1 = fragpart1.replaceAll(" ", "");
            fragpart1 = fragpart1.replaceAll("\t", "");
            
            if(fragpartOld1.equals(fragpart1)){
                return null;
            }else{
                return new FragmentChange(oldfrag.getId(), newfrag.getId(), fragpartOld, fragpart);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String transitPath(String filePath) {
        // TODO Auto-generated method stub
        int index = filePath.indexOf("jabref-2005-2009")+16;
        if(index==-1){ return filePath; }
        String ret = "C:/personal/CodeClonePrediction_TSE/data/jabref/jabref-all"+filePath.substring(index);
        return ret.replace('\\', '/');
    }
}
