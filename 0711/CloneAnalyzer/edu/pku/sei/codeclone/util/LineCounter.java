package edu.pku.sei.codeclone.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LineCounter {
    public static int linecount;
    public static void main(String args[]){
        String path = "C:/personal/CodeClonePrediction_TSE/data/ant_space/sources/1376403";
        lineCount(path);
        System.out.println(linecount);
    }
    public static void lineCount(String path){
        File f = new File(path);
        if(!f.isDirectory()){
            if(path.endsWith(".java")){
                linecount = linecount + count(f);
            }else{
                return;
            }
        }else{
            for(String name : f.list()){
                lineCount(path + "/" + name);
            }
        }
    }
    private static int count(File f) {
        // TODO Auto-generated method stub
        int sum = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            for(String line = br.readLine(); line!=null; line = br.readLine()){
                sum ++ ;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        System.out.println(f.getAbsolutePath() + ":" + sum);
        return sum;
    }
}
