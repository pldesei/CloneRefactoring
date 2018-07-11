package edu.pku.sei.codeclone.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class MoveDataForPublic {
    public static void main(String args[]){
        File originPath = new File("C:\\personal\\CodeClonePrediction_TSE\\data\\guitar_space\\outputs");
        File destPath = new File("C:\\personal\\CodeClonePrediction_TSE\\data\\data_open\\guitar");
        for(String subPath : originPath.list()){
            (new File(destPath.getAbsolutePath() + "\\" + subPath)).mkdirs();
            File copiedFile = new File(originPath.getAbsolutePath() + "\\" + subPath + "\\clones.xml");
            File pasteFile = new File(destPath.getAbsolutePath() + "\\" + subPath + "\\clones.xml");
            try {
                FileUtils.copyFile(copiedFile, pasteFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}  
