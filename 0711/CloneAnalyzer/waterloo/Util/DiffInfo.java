package waterloo.Util;

public class DiffInfo {
	String prefilename;
	String filename;

	 public DiffInfo(String pf, String f) {
		prefilename = formatFilePath(pf);
		filename = formatFilePath(f);
	}
	
	private String formatFilePath(String filePath){
		return filePath.replace("/", "\\");
	}
	
	public String getPreFilePath(){
		return prefilename;
	}
}
