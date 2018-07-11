package waterloo.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class PatchAnalyzer {
	public DiffInfo anayDiff(String diff) {
		String tdiff = diff;
		String before = tdiff.substring(tdiff.indexOf("--- ") + 6, tdiff.indexOf("+++ ") - 1);
		String after = tdiff.substring(tdiff.indexOf("+++ ") + 6, tdiff.indexOf("\n@@"));
		DiffInfo diffInf = new DiffInfo(before, after);
		return diffInf;
	}
	
	public Vector<DiffInfo> anayPatch(File patch) {
		Vector<DiffInfo> patchInf = new Vector<DiffInfo>();
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(patch);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] diffs = sb.toString().split("diff --git ");
		for (String diff : diffs) {
			if (diff.indexOf("+++ ") == -1)
				continue;
			patchInf.add(anayDiff(diff));
		}
		return patchInf;
	}
	

}
