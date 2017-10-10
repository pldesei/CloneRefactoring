import java.io.*;

public class reset {

	public static void work(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File tmp : files) {
				work(tmp.getAbsolutePath());
			}
		}
		else {
			String oldpath = file.getAbsolutePath();
			//System.out.println(oldpath);
			String suffix = oldpath.substring(oldpath.lastIndexOf(".")+1);
			if (oldpath.contains("cloneInstance") && !suffix.equals("java")) {
				String newpath = oldpath.substring(0, oldpath.lastIndexOf(".")) + ".java";
				System.out.println(newpath);
				if (new File(newpath).exists())
					new File(newpath).delete();
				file.renameTo(new File(newpath));
			}
		}
		
	}

	public static void main(String[] args) {
		String path = "C:\\CloneRefactoring\\hopelastchange\\";
		work(path);
	}
}