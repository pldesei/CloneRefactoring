package edu.pku.sei.codeclone.predictor.misc;

import java.util.ArrayList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class DiffTrial {
	public static void main(String args[]){
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		list1.add("abc");
		list1.add("def");
		list1.add("ghi");
		list2.add("ac");
		list2.add("def");
		list2.add("ghi");
		list2.add("qq");
		list2.add("pp");
		Patch patch = DiffUtils.diff(list1, list2);
		for(Delta del:patch.getDeltas()){
				System.out.println(del.toString());
		}
	}
}
