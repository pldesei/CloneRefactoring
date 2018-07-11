package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class MyVersionList {
    public static Hashtable<String, int[]> lineMapTable = new Hashtable<String, int[]>();
    
    public static List<MyVersion> load(String srcPath, String resPath) {
        List<MyVersion> versions = new ArrayList<MyVersion>();
        File srcFolder = new File(srcPath);
        int count = 1;
        //List<Integer> versionNums = new ArrayList<Integer>();
        List<String> versionNums = new ArrayList<String>();
        for(String versionName:srcFolder.list()){
        	if (!new File(srcPath + versionName).isDirectory()) {
        		continue;
        	}
            versionNums.add(versionName);
        }
        Collections.sort(versionNums, Collections.reverseOrder());
        for(String versionName:versionNums){
            System.out.println("loading version "+ count);
            MyVersion v = loadVersion(srcPath + versionName, resPath + versionName, count);
            versions.add(v);
            count++;
        }
        for(int i = 1; i<versions.size(); i++){
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
        // TODO Auto-generated method stub
        MyFragment tempFrag = buildPred(f, curVersion, lastVersion);
        if(tempFrag==null) return null;
        double maxSimi = 0.0;
        MyFragment maxFrag = null;
        for(MyCloneClass cc : lastVersion.getCloneClasses()){
            for(MyFragment f1 : cc.getFragments()){
                if(tempFrag.srcPath.equals(f1.srcPath)){
                    if(tempFrag.exactMatch(f1)){
                        return f1;
                    }else{
                        double simi = tempFrag.match(f1);
                        if(simi > 0 && simi > maxSimi){
                            maxSimi = simi;
                            maxFrag = f1;
                        }
                    }
     
                }                           
            }
        }
        return maxFrag;
    }
    private static List<String> getSrc(String path) {
        // TODO Auto-generated method stub
        List<String> lines = new ArrayList<String>();
        try{
            BufferedReader in = new BufferedReader(new FileReader(path));
            for(String line = in.readLine(); line!=null; line = in.readLine()){
                lines.add(line);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return lines;
    }

    private static MyFragment buildPred(MyFragment f, MyVersion curVersion, MyVersion lastVersion) {
        
        
        String curPath = f.srcPath;        
        String predPath = f.srcPath.replace(curVersion.basePath, lastVersion.basePath);
        
        File tempFile = new File(predPath);
        if(!tempFile.exists()){
            return null;
        }
        
        List<String> currentValue = getSrc(curPath);
        List<String> predValue = getSrc(predPath);
        int currentStart = f.startLine;
        int currentEnd = f.endLine;

        
        int[] linemap = MyVersionList.lineMapTable.get(curPath);
        if(linemap==null){
            
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
            if(currentStart<=linemap[i]){
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
        try {
            BufferedReader in = new BufferedReader(new FileReader(resPath + "\\a.xml"));
            //Hashtable<String, String> fileTable = new Hashtable<String, String>();
            MyCloneClass cc = null;
            /*for(String line = in.readLine(); line!=null; line = in.readLine()){
                /*if(line.startsWith("  <sourceFile")){
                    String id = getAttr(line, "id");
                    String srcFile = getAttr(line, "location").replace('\\', '/');
                    fileT/able.put(id, srcFile);
                }else if(line.startsWith("  <cloneClass")){
                    MyCloneClass mcc = new MyCloneClass();
                    v.addClass(mcc);
                    cc = mcc;
                }else if(line.startsWith("    <clone ")){
                    int lineCount = Integer.parseInt(getAttr(line, "lineCount"));
                    int startLine = Integer.parseInt(getAttr(line, "startLine"));
                    int endLine = startLine + lineCount;
                    String fileID = getAttr(line, "sourceFileId");
                    MyFragment frag = new MyFragment(startLine, endLine, fileTable.get(fileID), count, v.versionRepoID);
                    cc.addFrag(frag);
                }
            }*/
            String str = null;
            while ((str = in.readLine()) != null) {
                if (str.contains("<clone")) {// enter a clone group
                    MyCloneClass mcc = new MyCloneClass(v);
                    v.addClass(mcc);
                    cc = mcc;

                    str = in.readLine();
                    while (str.contains("<file>")) {// enter a clone instance
                        str = in.readLine();// read path
                        String filePath = str.substring(str.indexOf(">") + 1, str.indexOf("</"));

                        str = in.readLine(); // read start line
                        int startLine = Integer.parseInt(str.substring(str.indexOf('>') + 1, str.lastIndexOf('<')));
                        str = in.readLine(); // read end line
                        int endLine = Integer.parseInt(str.substring(str.indexOf('>') + 1, str.lastIndexOf('<')));

                        str = in.readLine(); // read start token
                        int startToken = Integer.parseInt(str.substring(str.indexOf('>') + 1, str.lastIndexOf('<')));
                        str = in.readLine(); // read end token
                        int endToken = Integer.parseInt(str.substring(str.indexOf('>') + 1, str.lastIndexOf('<')));
                        
                        MyFragment frag = new MyFragment(startLine, endLine, startToken, endToken, filePath, count, v.versionRepoID);
                        cc.addFrag(frag);
                        
                        // set text of this code snippet
                        in.readLine(); // read </file>, end of a clone instance
                        str = in.readLine();// Scenario1: read <file>, continue
                                            // the
                                            // inner while. Scenario2: read
                                            // </clone>, end the inner while
                    } // end of all clone instances in this clone group
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return v;
    }

    /*private static String getAttr(String line, String label) {
        // TODO Auto-generated method stub
        int index = line.indexOf(label + "=\"");
        String val = line.substring(index + label.length() + 2, line.indexOf("\"", index + label.length() + 2));
        return val;
    }*/

}
