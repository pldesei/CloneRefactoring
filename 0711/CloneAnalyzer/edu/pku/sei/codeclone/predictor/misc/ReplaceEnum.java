package edu.pku.sei.codeclone.predictor.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ReplaceEnum {
    public static void main(String args[]){
        String path = "C:/personal/CodeClonePrediction_TSE/data/xerces_space/sources";
        addEnum(path);
    }
    public static void addEnum(String path){
        if(path.endsWith(".java")){
            addEnum2File(path);
        }else{
            File f = new File(path);
            if(f.isDirectory()){
                for(String name : f.list()){
                    addEnum(path+"/"+name);
                }
            }else{
                return;
            }
        }
    }
    private static void addEnum2File(String path) {
        // TODO Auto-generated method stub
        int index1 = path.indexOf("sources/") + 8;
        String version = path.substring(index1, path.indexOf("/", index1 + 1));
        int x = Integer.parseInt(version);        
        System.out.println("handling " + path + " ...");

        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            List<String> lines = new ArrayList<String>();
            for(String line = in.readLine(); line!=null; line = in.readLine()){
                String line1 = replaceEnum(line);
                lines.add(line1);
            }
            in.close();
            PrintWriter out = new PrintWriter(new FileWriter(path));
            for(String line : lines){
                out.println(line);
            }
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    private static String replaceEnum(String line) {
        // TODO Auto-generated method stub
        String res = "";
        int index = line.indexOf("enum");
        if(index == -1) {return line;}
        List<String> segs = new ArrayList<String>();
        int lastIndex = 0;
        while(index != -1){
            if(isEdge(line, index-1)&&isEdge(line, index+4)){
                segs.add(line.substring(lastIndex, index));
                lastIndex = index + 4;
                index = line.indexOf("enum", lastIndex);                
            }else{
                index = line.indexOf("enum", index + 4);
            }
        }
        if(lastIndex == line.length()){
            segs.add("");
        }else{
            segs.add(line.substring(lastIndex));
        }
        for(int i = 0; i<segs.size()-1; i++){
            res += segs.get(i);
            res += "enum_a1b2c3";
        }
        res += segs.get(segs.size()-1);
        return res;
    }
    private static boolean isEdge(String line, int i) {
        // TODO Auto-generated method stub
        if(i<0 || i >= line.length()){
            return true;
        }else{
            char c = line.charAt(i);
            if(Character.isDigit(c)||Character.isLetter(c)||c=='_'){
                return false;
            }else{
                return true;
            }
        }
    }
}
