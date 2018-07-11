package edu.pku.sei.codeclone.predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import edu.pku.sei.codeclone.predictor.MyVersionList;

public class LineMapBuilder {
	public int[] buildLineMap(String curPath, String predPath) {

		File tempFile = new File(curPath);
		if (!tempFile.exists()) {
			return null;
		}
		tempFile = new File(predPath);
		if (!tempFile.exists()) {
			return null;
		}

		List<String> currentValue = getSrc(curPath);
		List<String> predValue = getSrc(predPath);
		int[] linemap = MyVersionList.lineMapTable.get(curPath);
		if (linemap == null) {

			Patch p = DiffUtils.diff(predValue, currentValue);
			List<Delta> deltas = p.getDeltas();

			for (int i = 0; i < deltas.size(); i++) {
				for (int j = i + 1; j < deltas.size(); j++) {
					if (deltas.get(i).getOriginal().getPosition() < deltas.get(j).getOriginal().getPosition()) {
						Delta d = deltas.get(i);
						deltas.set(i, deltas.get(j));
						deltas.set(j, d);
					}
				}
			}

			linemap = new int[predValue.size() + 1];
			int index1 = 0;
			int index2 = 0;

			for (int k = deltas.size() - 1; k >= 0; k--) {
				Delta del = deltas.get(k);
				int lineNumber = del.getOriginal().getPosition();
				int linesOld = del.getOriginal().getLines().size();
				int linesNew = del.getRevised().getLines().size();
				for (int i = index1; i < lineNumber; i++) {
					linemap[i] = index2;
					index2++;
				}
				index1 = lineNumber;
				if (del.getType().equals(Delta.TYPE.INSERT)) {
					index2 += linesNew;
				} else if (del.getType().equals(Delta.TYPE.DELETE)) {
					for (int i = index1; i < index1 + linesOld; i++) {
						linemap[i] = -1;
					}
					index1 += linesOld;
				} else if (del.getType().equals(Delta.TYPE.CHANGE)) {
					for (int i = index1; i < index1 + linesOld; i++) {
						linemap[i] = -1;
					}
					index1 += linesOld;
					index2 += linesNew;
				}
			}
			for (int i = index1; i < predValue.size(); i++) {
				linemap[i] = index2;
				index2++;
			}
			MyVersionList.lineMapTable.put(curPath, linemap);
		}

		return linemap;
	}

	private List<String> getSrc(String path) {
		// TODO Auto-generated method stub
		List<String> lines = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				lines.add(line);
			}
			lines.add("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

}
