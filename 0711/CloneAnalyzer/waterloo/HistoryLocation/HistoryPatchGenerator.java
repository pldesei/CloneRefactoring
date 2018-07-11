package waterloo.HistoryLocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import edu.pku.sei.codeclone.predictor.MyCloneClass;
import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.RefactoredInstance;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import waterloo.History.NewRefactorCommitLocator;
import waterloo.Util.GlobalSettings;
import waterloo.SortByVersion;
/*
 * git log  --pretty=format:"%H,%P,%cd,%s" --no merges >newCommit.txt
 */

public class HistoryPatchGenerator {

	class MyVersionNode {
		String currentHash, parentHash;
		ArrayList<String> parentHashList;

		public MyVersionNode(String currentHash, String parentHash) {
			this.currentHash = currentHash;
			this.parentHash = parentHash;
			this.parentHashList = new ArrayList<String>();
			// if(parentHash!=null) this.addParentHashToList(parentHash);
		}

		public void addParentHashToList(String hash) {
			this.parentHashList.add(hash);
		}

		public String getParentHash() {
			return this.parentHash;
		}

		public ArrayList<String> getParentHashList() {
			return this.parentHashList;
		}
	}

	String refactorFileLabel = "refactored";
	String unrefactorFileLabel = "unrefactored";
	
	long refactoredGroupCnt = 0, unrefactoredGroupCnt = 0;
	int totalPreCommitCnt = 5;
	String projectPath=null,filteredLogFilePath=null,totalLogFilePath=null;
	String cloneDataPath=null;
	String instanceFolderPath=null;
	
	String repoPath;
	
	String openFolderCmd = "cd ";
	String gitDiffCommand = "git diff";

	HashMap<String, MyVersionNode> hashVersionNodeMap;
	ArrayList<String> filteredHashList;
	
	public HistoryPatchGenerator(String projectPath,String filteredLogFilePath,String totalLogFilePath,String cloneDataPath, String repoPath) {
		this.projectPath=projectPath;
		this.filteredLogFilePath=filteredLogFilePath;
		this.totalLogFilePath=totalLogFilePath;
		this.cloneDataPath=cloneDataPath;
		this.instanceFolderPath=cloneDataPath+GlobalSettings.pathSep+"instanceInfos";
		this.repoPath = repoPath;
		
		hashVersionNodeMap = new HashMap<String, MyVersionNode>();
		filteredHashList = new ArrayList<String>();
		setHashVersionNodeMap();
		setFilteredHashList();
		updateHistoryForFilteredHashList();
	}

	private void outputCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(new Date()));
	}
	/*
	 * Goal:read logFile, and store <hashValue,MyVersionNode> to hashVersionNodeMap
	 */
	private void setHashVersionNodeMap() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(this.totalLogFilePath));
			String line = null;
			while ((line = in.readLine()) != null) {
				String[] tmp = line.split(",");

				MyVersionNode versionNode;
				if (!tmp[1].contains(" "))// one parent
					versionNode = new MyVersionNode(tmp[0], tmp[1]);
				else {// multiple parents, select only one parent
					versionNode = new MyVersionNode(tmp[0], tmp[1].substring(0, tmp[1].indexOf(" ")));
				}
				hashVersionNodeMap.put(tmp[0], versionNode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Goal:read filteredLogFile, and add hashValue to filteredHashList
	 */
	private void setFilteredHashList() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(this.filteredLogFilePath));
			String line = null;
			while ((line = in.readLine()) != null) {
				String[] tmp = line.split(",");
				filteredHashList.add(tmp[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * Goal:update MyVersionNode whose corresponding hashValue in hashVersionMap exists in filteredHashList
	 */
	private void updateHistoryForFilteredHashList() {
		for (String hashStr : filteredHashList) {
			MyVersionNode versionNode = hashVersionNodeMap.get(hashStr);
			String pointerHash = null;
			if (versionNode != null)
				pointerHash = versionNode.getParentHash();
			for (int i = 0; i <= totalPreCommitCnt; i++) {
				if (pointerHash != null) {
					versionNode.addParentHashToList(pointerHash);
					MyVersionNode pointerNode = hashVersionNodeMap.get(pointerHash);
					if (pointerNode != null)
						pointerHash = pointerNode.getParentHash();
				}
			}
		}
	}

	public void genPrePatches() {
		try {
			String batFilePath = cloneDataPath + GlobalSettings.pathSep+"genHisPatches.sh";
			PrintWriter pw = new PrintWriter(new FileWriter(batFilePath));
			pw.println(openFolderCmd+this.projectPath);

			File cloneFileFolder = new File(cloneDataPath);
			ArrayList<File> unrefactorCloneFileList = new ArrayList<File>();
			ArrayList<File> refactorCloneFileList = new ArrayList<File>();
			for (File cloneFile : cloneFileFolder.listFiles()) {
				String fileName = cloneFile.getName();
				if (fileName.contains("readable"))
					continue;
				if (fileName.contains(unrefactorFileLabel))
					unrefactorCloneFileList.add(cloneFile);
				else if (fileName.contains(refactorFileLabel))
					refactorCloneFileList.add(cloneFile);
			}
			Collections.sort(unrefactorCloneFileList, new SortByVersion());
			Collections.sort(refactorCloneFileList, new SortByVersion());
			System.out.print("afterReadInstanceData:");
			outputCurrentTime();
			
			 for (File cloneFile : unrefactorCloneFileList) {
			 processSingleCloneFile(cloneFile, unrefactorFileLabel,pw); }
			 System.out.print("afterProcessingUnRefactorClone:");
			 outputCurrentTime();
			
			for (File cloneFile : refactorCloneFileList) {
				processSingleCloneFile(cloneFile, refactorFileLabel, pw);
			}
			System.out.print("afterProcessingRefactorClone:");
			outputCurrentTime();
			pw.close();
			generateHistoricalPatches(batFilePath);
			System.out.print("afterRunBat(End):");
			outputCurrentTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSingleCloneFile(File cloneFile, String cloneFileLabel, PrintWriter pw) {
		Vector<RefactoredInstance> refactoredInsList = new Vector<RefactoredInstance>();
		List<MyCloneClass> unrefactoredCloneList = new ArrayList<MyCloneClass>();

		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
			if (cloneFileLabel.equals(refactorFileLabel))
				refactoredInsList = (Vector<RefactoredInstance>) ois.readObject();
			else
				unrefactoredCloneList = (List<MyCloneClass>) ois.readObject();
			String repoFolderName = null;
			if (cloneFileLabel.equals(refactorFileLabel)) {
				for (RefactoredInstance refactoredIns : refactoredInsList) {
					repoFolderName = refactoredIns.getCommonMethod().getVersionRepoName();
					Vector<MyFragment> frags = refactoredIns.getFragments();
					processSingleGroupPreviousCommits(repoFolderName, frags, cloneFileLabel, pw,refactoredIns);
				}
			} else {
				for (MyCloneClass clazz : unrefactoredCloneList) {
				  List<MyFragment> frags = clazz.getFragments();
				 repoFolderName=frags.get(0).getVersionRepoName();
				 processSingleGroupPreviousCommits(repoFolderName, frags, cloneFileLabel, pw,null);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSingleGroupPreviousCommits(String repoFolderName, List<MyFragment> frags, String cloneFileLabel,
			PrintWriter pw,RefactoredInstance refactoredIns) {
		// Set historical patch info
		String hashStr =null;
		if(refactoredIns==null)
			hashStr=filteredHashList.get(Integer.parseInt(repoFolderName)-1);
		else {
			System.out.print("beforeGetRealRefactorCommit:");
			outputCurrentTime();
			NewRefactorCommitLocator locator=new NewRefactorCommitLocator(this.projectPath,this.filteredLogFilePath,this.totalLogFilePath, this.repoPath);
			locator.setHashvalueList();
			hashStr=locator.getRealChangeID(refactoredIns, GlobalSettings.simi);
			System.out.print("afterGetRealRefactorCommit:");
			outputCurrentTime();
		}
		MyVersionNode versionNode = hashVersionNodeMap.get(hashStr);
		ArrayList<String> parentHashList = versionNode.getParentHashList();
		
		// Create folder for storing historical patches
		if (!isSingleGroupParserable(frags))
			return;
		String newCloneFileLabel = null;
		if (cloneFileLabel.equals(refactorFileLabel)) {
			refactoredGroupCnt++;
			newCloneFileLabel = "refactor" + refactoredGroupCnt;
		} else if (cloneFileLabel.equals(unrefactorFileLabel)) {
			unrefactoredGroupCnt++;
			newCloneFileLabel = "unrefactor" + unrefactoredGroupCnt;
		}
		String oneInstanceFolderPath = instanceFolderPath + GlobalSettings.pathSep+"" + newCloneFileLabel;
		// Generate historical patches
		String historicalPatchFolderPath = oneInstanceFolderPath + GlobalSettings.pathSep+"realHistoricalPatches";
		File historicalPatchFolder = new File(historicalPatchFolderPath);
		if (!historicalPatchFolder.exists())
			historicalPatchFolder.mkdir();	
		String parentHash = null, currentHash = hashStr;
		for (int i = 0; i < parentHashList.size(); i++) {
			parentHash = parentHashList.get(i);
			pw.println(gitDiffCommand + " " + parentHash + " " + currentHash + ">" + historicalPatchFolderPath
					+ GlobalSettings.pathSep+"patch" + i + ".txt");
			currentHash = parentHash;
		}

	}

	private void generateHistoricalPatches(String batFilePath) {
		try {
			Runtime runtime = Runtime.getRuntime();
			Process p = runtime.exec("bash " + batFilePath);
			final InputStream is1 = p.getErrorStream();
			new Thread(new Runnable() {
				public void run() {
					BufferedReader br = new BufferedReader(new InputStreamReader(is1));
					try {
						while (br.readLine() != null)
							;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			//need to read the output, or no terminate
			InputStream is2 = p.getInputStream();
			BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
			StringBuilder buf = new StringBuilder();
			String line = null;
			while ((line = br2.readLine()) != null)
				buf.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isSingleGroupParserable(List<MyFragment> frags) {
		for (MyFragment frag : frags) {
			if (isParserable(frag))
				return true;
		}
		return false;
	}

	private boolean isParserable(MyFragment frag) {
		File file = new File(frag.getFilePath());
		try {
			CompilationUnit cu = JavaParser.parse(file);
		} catch (Exception e) {
			return false;
		} catch (Error e) {
			return false;
		}
		return true;
	}

}
