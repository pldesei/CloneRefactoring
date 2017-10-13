import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class replaceCloneInstance {
	public static void replace(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File tmp : files) {
				replace(tmp.getAbsolutePath());
			}
		} else {

			String oldFilePath = file.getAbsolutePath();
			System.out.println("OldFilePath:" + oldFilePath);
			String newFilePath = oldFilePath;
			if (oldFilePath.contains("cloneFragment")) {
				newFilePath = oldFilePath.replaceAll("cloneFragment", "cloneMethod");
			}
			System.out.println("NewFilePath:" + newFilePath);
			File newFile = new File(newFilePath);
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
				String str = null;
				while ((str = br.readLine()) != null) {
					if (str.startsWith("This clone fragment is located in File")
							|| str.startsWith("The line range of this clone fragment is")
							|| str.startsWith("The content of this clone fragment is as follows")) {
						bw.write(str.replaceAll("clone fragment", "clone method"));
					} else
						bw.write(str);
					bw.write(System.getProperty("line.separator"));
				}
				br.close();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (newFile.exists())
				file.delete();
		}

	}

	public static void main(String[] args) {
		// Copy C:\CloneRefactoring\hopelastchange\elasticsearch, guava, jmeter,
		// lucene and org.eclipse.emf(five projects) to
		// C:\CloneRefactoring\CloneComparison\ManuEvaluation
		String path = "C:\\CloneRefactoring\\ManuEvaluation\\";
		replace(path);
	}
}
