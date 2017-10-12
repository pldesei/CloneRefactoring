import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class repalceCloneInstance {
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
			if (oldFilePath.contains("cloneInstance")) {
				newFilePath = oldFilePath.replaceAll("cloneInstance", "cloneFragment");
			}
			System.out.println("NewFilePath:" + newFilePath);
			File newFile = new File(newFilePath);
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
				String str = null;
				while ((str = br.readLine()) != null) {
					if (str.startsWith("This clone instance is located in File")
							|| str.startsWith("The line range of this clone instance is")
							|| str.startsWith("The content of this clone instance is as follows")) {
						bw.write(str.replaceAll("clone instance", "clone fragment"));
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
		String path = "C:\\CloneRefactoring\\CloneComparison\\";
		replace(path);
	}
}
