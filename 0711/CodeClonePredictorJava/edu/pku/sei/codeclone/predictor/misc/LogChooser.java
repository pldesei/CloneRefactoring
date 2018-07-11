package edu.pku.sei.codeclone.predictor.misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class LogChooser {
        public static void main(String args[]){
            String versionLogPath = "C:/personal/CodeClonePrediction_TSE/data/guitar_log.txt";
            String versionOutputPath = "C:/personal/CodeClonePrediction_TSE/data/guitar_log_simple.txt";

            try{
                BufferedReader in = new BufferedReader(new FileReader(versionLogPath));
                PrintWriter pw = new PrintWriter(new FileWriter(versionOutputPath));
                String curVersion = "";
                String curDate = "";
                Date now = (new SimpleDateFormat("yyyy-MM-dd")).parse("2012-08-27");
                Stack<String> versionlist = new Stack<String>();
                Stack<String> versiontable = new Stack<String>();

                for(String line = in.readLine(); line!=null; line = in.readLine()){
                    if(line.startsWith("Revision:")){
                        curVersion = line.substring(9).trim();
                  //      System.out.println(curVersion);
                    }
                    if(line.startsWith("Date:")){
                        curDate = line.substring(line.indexOf(':')+1).trim();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        //System.out.println(curDate);
                        curDate = getDateStr1(curDate);
                        Date curd = formatter.parse(curDate);
                        if(curd.before(now)){
                            versionlist.add(curVersion);
                            versiontable.add(curVersion+ "\t"+curDate);
                            while(curd.before(now)){
                                weekago(now);
                            }
                        }
                    }
                    
                }
                versionlist.pop();
                while(!versionlist.empty()){
                    String versionID = versionlist.pop();
                    System.out.println("svn update -r " + versionID);
                    System.out.println("md \\"+versionID);
                    System.out.println("echo d | xcopy /s . ..\\"+versionID);
                //    System.out.println("java DelNonJava C:\\personal\\CodeClonePrediction_TSE\\data\\jedit_all\\"+versionID);
                }
                while(!versiontable.empty()){
                    pw.println(versiontable.pop());
                }
                pw.close();
                
            }catch(IOException e){
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        private static void weekago(Date now) {
            // TODO Auto-generated method stub
            now.setTime(now.getTime()-1000*3600*24*7);
        }
        private static String getDateStr(String curDate) {
            int index1 = curDate.indexOf("--");
            String year = curDate.substring(0, index1);
            int index2 = curDate.indexOf("||", index1);
            int index3 = curDate.indexOf(' ', index2+2);
            String mon = curDate.substring(index1+2, index2);
            String day = curDate.substring(index2+2, index3);
            if(mon.length()==1){
                mon = "0" + mon;
            }
            if(day.length()==1){
                day = "0" + day;
            }
            return year + '-' + mon + '-' + day;
        }
        private static String getDateStr1(String curDate) {
            String[] items = curDate.trim().split(" ");
            String mon = getMonth(items[1]);
            String day = items[2].substring(0, items[2].length()-1);
            String year = items[3];
            
            return year + '-' + mon + '-' + day;
        }
        private static String getMonth(String str) {
            if(str.startsWith("Jan")){
                return "01";
            }else if(str.startsWith("Feb")){
                return "02";
            }else if(str.startsWith("Mar")){
                return "03";
            }else if(str.startsWith("Apr")){
                return "04";
            }else if(str.startsWith("May")){
                return "05";
            }else if(str.startsWith("Jun")){
                return "06";
            }else if(str.startsWith("Jul")){
                return "07";
            }else if(str.startsWith("Aug")){
                return "08";
            }else if(str.startsWith("Sep")){
                return "09";
            }else if(str.startsWith("Oct")){
                return "10";
            }else if(str.startsWith("Nov")){
                return "11";
            }else if(str.startsWith("Dec")){
                return "12";
            }else{
                return "";
            }
        }

}
