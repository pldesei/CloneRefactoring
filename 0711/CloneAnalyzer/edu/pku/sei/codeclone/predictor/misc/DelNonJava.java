package edu.pku.sei.codeclone.predictor.misc;

import java.io.File;
import java.io.IOException;

public class DelNonJava {
    public static void main(String args[]){
        String path = "C:/personal/CodeClonePrediction_TSE/data/tomcat_space";
        delNonJava(path);
    }

    private static void delNonJava(String path) {
        // TODO Auto-generated method stub
        File f = new File(path);
        if(f.isDirectory()){
            System.out.println(f);
            for(String name:f.list()){
                String subPath = path + "/" + name;
                File x = new File(subPath);
                if(x.isDirectory()){
                    delNonJava(subPath);
                }else if(subPath.endsWith(".java")){
                    System.out.println(subPath);
                }else{
                    Process process;
                    try {
                        System.err.println(subPath);
                        String cmd = "cmd.exe /c del " + subPath.replace('/', '\\');
                        process = Runtime.getRuntime().exec(cmd);
                        process.waitFor(); 
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } 
                }
            }
        }
    }
}
