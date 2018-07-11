package edu.pku.sei.codeclone.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

public class PackageCount {
	public static Hashtable<String, Integer> packTable = new Hashtable<String, Integer>();
    public static void main(String args[]){
    	String pack = "android:xml:abc";
    	String[] arr = pack.split(":");
    	System.out.println(arr);
//        String path = "C:/personal/CodeClonePrediction_TSE/data/ant_space/sources/1376403";
 //       lineCount(path);
 //       System.out.println();
    	System.out.println();
    }
    public static void lineCount(String path){
        File f = new File(path);
        if(!f.isDirectory()){
            if(path.endsWith(".java")){
            	count(f);
            }else{
                return;
            }
        }else{
            for(String name : f.list()){
                lineCount(path + "/" + name);
            }
        }
    }
    private static void count(File f) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            for(String line = br.readLine(); line!=null; line = br.readLine()){
            	if(line.startsWith("import ") && line.indexOf(";")!=-1){
            		String pack = line.substring(line.indexOf("import ")+7, line.indexOf(";"));
            		String[] packArr = pack.split(".");
            		if(packArr.length>=3 && !packArr[0].equals("java")){
            			add2table(packArr[0] + "." + packArr[1] + "." + packArr[2]);
            		}else if(packArr.length >=2 && packArr[0].equals("java")){
            			add2table(packArr[0] + "." + packArr[1]);            			
            		}else{
            			add2table(pack);
            		}
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	private static void add2table(String str) {
		if(packTable.get(str)==null){
			packTable.put(str, 1);
		}else{
			packTable.put(str, packTable.get(str) + 1);
		}
	}
	public static void sortValue(Hashtable<?, Integer> t){
	   ArrayList<Map.Entry<?, Integer>> l = new ArrayList(t.entrySet());
	   Collections.sort(l, new Comparator<Map.Entry<?, Integer>>(){
	       public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
	           return o1.getValue().compareTo(o2.getValue());
	        }});
	   for(Map.Entry<?, Integer> entry:l){
	       System.out.println(entry.getKey() + ":" + entry.getValue());
	   }
    }
}