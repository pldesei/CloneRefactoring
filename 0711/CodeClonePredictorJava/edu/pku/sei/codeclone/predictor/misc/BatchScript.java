package edu.pku.sei.codeclone.predictor.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BatchScript {
    public static void main(String args[]){
        String homePath = "C:/personal/CodeClonePrediction_TSE/data/guitar_space";
        File path = new File(homePath + "/sources");
        for(String name:path.list()){
             try {
                PrintWriter pw = new PrintWriter(new FileWriter(homePath + "/scripts/"+name+".cqr"));
                pw.println("#!org.conqat.engine.code_clones.languages.java.JavaCloneAnalysis");
                pw.println("clone.minlength=10");
                String outDir = homePath+"/outputs/out_"+name;
                File out = new File(outDir);
                if(!out.exists()){
                    out.mkdirs();
                }
                pw.println("output.dir="+outDir);
                pw.println("input.project=guitar");
                pw.println("input.dir="+homePath+"/sources/"+name);
                pw.println();
                pw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("call conqat.bat -f ..\\..\\..\\data\\guitar_space\\scripts\\"+name+".cqr");
        }
    }
}
