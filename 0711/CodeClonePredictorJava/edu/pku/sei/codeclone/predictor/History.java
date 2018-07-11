package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.io.Serializable;

public class History implements Serializable{
    private static final long serialVersionUID = 1L;

    List<HistoryFrag> frags = new ArrayList<HistoryFrag>();
    Hashtable<String, Date> versionTable = new Hashtable<String, Date>();
    long length;
    long fileLength;
    List<String> versionList;
    public History(List<String> versionList, String versionLogPath){
        this.versionList = versionList;
        try{
            BufferedReader in = new BufferedReader(new FileReader(versionLogPath));
            String curVersion = "";
            String curDate = "";
            /*if(versionLogPath.endsWith("simple.txt")){
                for(String line = in.readLine(); line!=null; line = in.readLine()){
                    String versionName = line.substring(0, line.indexOf('\t'));
                    String date = line.substring(line.indexOf('\t')+1);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    this.versionTable.put(versionName, formatter.parse(date));
                }
            }else{
                for(String line = in.readLine(); line!=null; line = in.readLine()){
                    if(line.startsWith("Revision:")){
                        curVersion = line.substring(9).trim();
                    }
                    if(line.startsWith("Date:")){
                        curDate = line.substring(line.indexOf(',')+1).trim();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        curDate = getDateStr(curDate);
                        Date curd = formatter.parse(curDate);
                        this.versionTable.put(curVersion, curd);
                    }
                }
            }
            */
            String line = null;
            int cnt = 1;
            while ((line = in.readLine()) != null) {
            	if (cnt % 10 == 0) {
            		String[] tmp = line.split(",");
            		String versionName = tmp[0];
                    String date = tmp[1];
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
                    //String vname = (char)(cnt/10 + 'a') + (cnt%10 + "");
                    this.versionTable.put(cnt / 10 + "", formatter.parse(date));
            	}
            	cnt ++;
            }
            in.close();
        }catch(IOException e){
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /*private String getDateStr(String curDate) {
        // TODO Auto-generated method stub
        int index1 = curDate.indexOf(' ');
        String mon = curDate.substring(0, index1);
        int index2 = curDate.indexOf(", ", index1);
        int index3 = curDate.indexOf(' ', index2+2);
        String day = curDate.substring(index1+1, index2);
        String year = curDate.substring(index2+2, index3);
        if(mon.startsWith("Jan")){
            mon = "01";
        }else if(mon.startsWith("Feb")){
            mon = "02";
        }else if(mon.startsWith("Mar")){
            mon = "03";
        }else if(mon.startsWith("Apr")){
            mon = "04";
        }else if(mon.startsWith("May")){
            mon = "05";
        }else if(mon.startsWith("Jun")){
            mon = "06";
        }else if(mon.startsWith("Jul")){
            mon = "07";
        }else if(mon.startsWith("Aug")){
            mon = "08";
        }else if(mon.startsWith("Sep")){
            mon = "09";
        }else if(mon.startsWith("Oct")){
            mon = "10";
        }else if(mon.startsWith("Nov")){
            mon = "11";
        }else if(mon.startsWith("Dec")){
            mon = "12";
        }
        return year + '-' + mon + '-' + day;
    }*/

    public long getFileLength(int curVersion){
        if(frags.size()==0){
            this.fileLength = 0;
            return 0;
        }else{
            int originVersion = this.frags.get(frags.size()-1).versionID;
            this.fileLength = getDays(originVersion, curVersion);
            return this.fileLength;
        }
    }
    private long getDays(int version1, int version2){
        String v1 = this.versionList.get(version1 - 1);
        v1 = v1.substring(v1.lastIndexOf('\\') + 1);
        //v1 = v1.startsWith("0")?v1.substring(1):v1;
        String v2 = this.versionList.get(version2 - 1);
        v2 = v2.substring(v2.lastIndexOf('\\') + 1);
        //v2 = v2.startsWith("0")?v2.substring(1):v2;
    	//String v1 = version1 + "";
    	//String v2 = version2 + "";
        Date origin = this.versionTable.get(v1);
        Date current = this.versionTable.get(v2);
        Calendar originc = Calendar.getInstance();
        originc.setTime(origin);
        Calendar currentc = Calendar.getInstance();
        currentc.setTime(current);
        return (currentc.getTimeInMillis() - originc.getTimeInMillis())/(3600*24*1000);
    }
    
    public long getLength(int curVersion){
        if(frags.size()==0){
            this.length = 0;
            return 0;
        }else{
            int originVersion  = -1;
            for(int i = 0; i < frags.size(); i++){
                HistoryFrag f = frags.get(i);
                if(f.start == -1 && i == 0){
                    this.length = 0;
                    return 0;    
                }else if(f.start != -1){
                    originVersion = frags.get(i).versionID;
                }
            }
            this.length = getDays(originVersion, curVersion);
            return this.length;
        }
    }
    public int getChange(){
        int changes = 0;
        for(HistoryFrag frag:frags){
            if(frag.changed == 1){
                changes++;
            }
        }
        return changes;
    }
    public int getFilesChange(){
        int changes = 0;
        for(HistoryFrag frag:frags){
            if(frag.fileChanged){
                changes++;
            }
        }
        return changes;
    }
    public int getRecentChange(){
        if(this.frags.size() == 0){ return 0;}
        int curVersion = this.frags.get(0).versionID + 1;
        int changes = 0;
        long recent = this.length/4;
        for(HistoryFrag frag:frags){
            if(getDays(frag.versionID, curVersion)<recent && frag.changed == 1){
                changes++;
            }
        }
        return changes;        
    }
    
    public int getRecentFileChange(){
        if(this.frags.size() == 0){ return 0;}
        int curVersion = this.frags.get(0).versionID + 1;
        int changes = 0;
        long recent = this.fileLength/4;
        for(HistoryFrag frag:frags){
            if(frag.fileChanged && getDays(frag.versionID, curVersion)<recent){
                changes++;
            }
        }
        return changes;
    }

    public void addFrag(HistoryFrag predHistoryFrag) {
        // TODO Auto-generated method stub
        this.frags.add(predHistoryFrag);
    }
}
