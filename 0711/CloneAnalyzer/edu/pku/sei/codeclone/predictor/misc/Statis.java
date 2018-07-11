package edu.pku.sei.codeclone.predictor.misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Statis {
    public static void main(String args[]){
        try {
            BufferedReader in = new BufferedReader(new FileReader("C:/personal/CodeClonePrediction_TSE/data/icse/icse/results"));
            int steps = 20;
            double step = 1.0/steps;
            int[] t2t = new int[steps];
            int[] t2f = new int[steps];
            int[] f2t = new int[steps];
            int[] f2f = new int[steps];
            int sum = 0;
            int allt2t = 0;
            int allt2f = 0;
            int allf2t = 0;
            int allf2f = 0;
            
            for(String line = in.readLine(); line!=null; line = in.readLine()){
                sum++;
                StringTokenizer st = new StringTokenizer(line);
                
//                    String id = st.nextToken();
                    String truth = st.nextToken();
                    String pred = st.nextToken();
                    String flag = st.nextToken();
                    double d = -1;
                    if(flag.equals("+")){
                        d = Double.parseDouble(st.nextToken());
                    }else{
                        d = Double.parseDouble(flag);
                    }
                    if(d==1.0){
                        d = 0.999;
                    }
                    int x = (int)Math.floor(d/step);
                    if(truth.indexOf("good")!=-1){
                        if(pred.indexOf("good")!=-1){
                            t2t[x]++;
                            allt2t++;
                        }else{
                            t2f[x]++;
                            allt2f++;

                        }
                    }else{
                        if(pred.indexOf("good")!=-1){
                            f2t[x]++;
                            allf2t++;

                        }else{
                            f2f[x]++;
                            allf2f++;

                        }
                    }
            }
            int sumT2T = 0;
            int sumT2F = 0;
            int sumF2T = 0;
            int sumF2F = 0;

            
            for(int i = 1; i<steps+1; i++){
                System.out.println(1.0*i/steps);
                sumT2T += t2t[i-1];
                sumT2F += t2f[i-1];
                sumF2T += f2t[i-1];
                sumF2F += f2f[i-1];
                int remain = sum - (sumT2T+sumT2F+sumF2T+sumF2F);
                System.out.println("approve rate = " + 1.0*remain/sum);
                int remainT = (allt2t-sumT2T)+(allt2f-sumT2F);
                System.out.println("precision = " + 1.0*remainT/remain);
                System.out.println("block rate = " + 1.0*(sumT2T+sumT2F+sumF2T+sumF2F)/sum);
                System.out.println("recall = " + 1.0*(sumF2T+sumF2F)/(allf2t+allf2f));
                
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
