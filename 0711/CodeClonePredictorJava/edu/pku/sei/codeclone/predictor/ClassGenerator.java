package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ClassGenerator {
	public static void main(String args[]){
		//String classFile = "C:/personal/CodeClonePrediction_TSE/data/icse/icse/jfreechart-classes-final-2.arff";
		//String simpLogFile = "C:/personal/CodeClonePrediction_TSE/data/jfreechart_log_simple.txt";
		//String changeObj = "C:/personal/CodeClonePrediction_TSE/data/icse/icse/jfreechart-changes-object.cgs";
		String classFile = "D:\\f\\jfreechart-classes-final-2.arff";
		String simpLogFile = "D:\\f\\commitInfo.txt";
		String changeObj = "D:\\f\\guitar-changes-object.cgs";
		try{
		    int firstVer = 0;
		    int lastTrain = 0;
		    int lastCheck = 0;
		    BufferedReader in = new BufferedReader(new FileReader(simpLogFile));
		    in.close();
		    /*firstVer = Integer.parseInt(in.readLine().split("\t")[0]);
		    for(String line = in.readLine(); line!=null; line = in.readLine()){
		        if(!line.trim().equals("")){
		            int ver = Integer.parseInt(line.split("\t")[0]);
                    String date = line.split("\t")[1];
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
                    Date d = formatter.parse(date);
                    if(!d.after(formatter.parse("2008-08-27"))){
                        lastTrain = ver;
                    }
                    if(!d.after(formatter.parse("2010-08-27"))){
                        lastCheck = ver;
                    }
		        }
		    }
		    */
		    firstVer = 2;
		    lastTrain = 0;
		    lastCheck = 0;
		    
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(changeObj)));
			List<CloneFamily> families = (List<CloneFamily>)ois.readObject();
			ois.close();
			
			PrintWriter pwC = new PrintWriter(new FileWriter(classFile));
				pwC.println("@relation cloneEval");
				pwC.println();
				pwC.println("@attribute versionNum real");
				pwC.println("@attribute fileVersionNum real");
				pwC.println("@attribute changeNum real");
				pwC.println("@attribute fileChangeNum real");
				pwC.println("@attribute recentChangeNum real");
                pwC.println("@attribute recentFileChangeNum real");
                pwC.println("@attribute isTest {true, false}");
                pwC.println("@attribute lineNum real");
                pwC.println("@attribute invokeNum real");
                pwC.println("@attribute libInvokeNum real");
                pwC.println("@attribute localInovkeNum real");
                pwC.println("@attribute otherInvokeNum real");
                pwC.println("@attribute fieldNum real");
                pwC.println("@attribute paraNum real");
                pwC.println("@attribute fileSimi real");
                pwC.println("@attribute methodSimi real");
                pwC.println("@attribute maxParaSimi real");
                pwC.println("@attribute sumParaSimi real");
                pwC.println("@attribute markFileSimi real");
                pwC.println("@attribute localClone {true, false}");
//              pwC.println("@attribute localMethodClone {true, false}");
                pwC.println("@attribute postfix {true, false}");
                pwC.println("@attribute cloneEval {good, bad}");
				pwC.println();
				pwC.println("@data");
			int count = 0;
			List<CloneVersion> cvlist = new ArrayList<CloneVersion>();
			for(CloneFamily cf:families){
//			    cvlist.add(new CloneVersion(cf.startFragments.get(0).getVersionID(), cf.getConsistent(lastCheckVer)>0?"bad":"good"));
			    
			    if(cf.startFragments.get(0).getVersionID()<=lastTrain && cf.startFragments.get(0).getVersionID() != firstVer){
			        System.out.println(count++);
			        //	pwC.println("-------------------------------");
			        //	pwC.println("Family ID:"+cf.id);
			        //	pwC.println("Number of Changes: "+cf.vChanges.size());
			        int size = cf.getConsistent(lastCheck);
			        String cloneEval = size>0?"bad" + size:"good";
			        cf.buildFeature();
			        pwC.println(cf.startFeature.toString()+","+cloneEval);
			        cf.startFeature = null;
//			        System.gc();
			    }
			}
			Collections.sort(cvlist);
			for(CloneVersion x : cvlist){
			    pwC.println(x.version + "    " + x.flag);
			}
			pwC.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
