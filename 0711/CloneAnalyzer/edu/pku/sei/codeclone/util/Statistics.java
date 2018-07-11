package edu.pku.sei.codeclone.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class Statistics {
    List<Result> reses;
    public static double[] highs= {0.8, 0.85, 0.9, 0.95, 0.99};
    public static double[] lows= {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8};
    public static int[] highper= {5, 6, 7};
    public static int[] lowper= {1, 2, 3};

    public static void main(String args[]){
        String projName = "guitar_jfreechart";
        String projPath = "C:/personal/CodeClonePrediction_TSE/data/icse/icse/statis/" + projName + ".txt";
        Statistics st = new Statistics();
        st.drive(projPath);
        for(double d : Statistics.highs){
            System.out.println("\\hline " + projName + "(" + d + ")" + "            &" + st.getApprove(d) + "\\%  &" + st.getPrecision(d) + "\\% \\\\");
        }
        for(double d : Statistics.lows){
            System.out.println("\\hline " + projName + "(" + d + ")" + "            &" + st.getBlock(d) + "\\%  &" + st.getRecall(d) + "\\% \\\\");
        }        
        for(int d : Statistics.highper){
            System.out.println(d + ":" + st.getPrecision(d));
        }
        for(int d : Statistics.lowper){
            System.out.println(d + ":" + st.getRecall(d));
        }        

    }
    public void drive(String path){
        String resultPath = path;
        this.reses = new ArrayList<Result>();
        try{
            BufferedReader in = new BufferedReader(new FileReader(resultPath));
            for(String line = in.readLine(); line!=null; line = in.readLine()){
                line = line.replace('+', ' ');
                line = line.replace('*', ' ');
                StringTokenizer st = new StringTokenizer(line);
                Result res = new Result(st.nextToken(), st.nextToken(), st.nextToken(), Double.parseDouble(st.nextToken())); 
                reses.add(res);
            }
            Collections.sort(reses);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public double getPrecision(int percent){
        int num = reses.size() * percent /10;
        int bad = 0;
        for(int i = reses.size()-1; i >= reses.size() - num; i--){
            Result r = reses.get(i);
            if(r.fact.equals("2:bad")){
                bad++;
            }
        }
        return 100.0*(num-bad)/num;
    }
    public double getPrecision(double score){
        int all = 0;
        int bad = 0;
        for(Result r : reses){
            if(r.score >= score){
                all++;
                if(r.fact.equals("2:bad")){
                    bad++;
                }
            }
        }
        return 100.0*(all-bad)/all;
    }
    public double getApprove(double score){
        int all = 0;
        for(Result r : reses){
            if(r.score >= score){
                all++;
            }
        }
        return 100.0*all/reses.size();
    }
    public double getRecall(int percent){
        int num = reses.size() * percent /10;
        int bad = 0;
        int badall = 0;
        for(int i = 0; i < num; i++){
            Result r = reses.get(i);
            if(r.fact.equals("2:bad")){
                bad++;
            }
        }
        for(Result r:reses){
            if(r.fact.equals("2:bad")){
                badall++;
            }
        }
        return 100.0*bad/badall;
    }
    public double getRecall(double score){
        int all = 0;
        int rec = 0;
        for(Result r : reses){
            if(r.fact.equals("2:bad")){
                all++;
                if(r.score < score){
                    rec++;
                }
            }
        }
        return 100.0*rec/all;
    }
    public double getBlock(double score){
        int all = 0;
        for(Result r : reses){
            if(r.score < score){
                all++;
            }
        }
        return 100.0*all/reses.size();
    }

    public class Result implements Comparable<Result>{
        public String number;
        public String fact;
        public String pred;
        public double score;
        public Result(String number, String fact, String pred, double score){
            this.number = number;
            this.fact = fact;
            this.pred = pred;
            this.score = score;
        }
        @Override
        public int compareTo(Result arg0) {
            Result r = arg0;
            if (this.score > r.score) {
                return 1;
            }else if(this.score < r.score){
                return -1;
            }else{
                return 0;
            }
        }
    }
}
