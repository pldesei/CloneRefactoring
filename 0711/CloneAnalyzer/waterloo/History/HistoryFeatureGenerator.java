package waterloo.History;

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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import edu.pku.sei.codeclone.predictor.MyCloneClass;
import edu.pku.sei.codeclone.predictor.MyFragment;
import edu.pku.sei.codeclone.predictor.RefactoredInstance;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import mcidiff.main.TokenMCIDiff;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.TokenMultiset;
import waterloo.SortByVersion;
import waterloo.Util.GlobalSettings;

public class HistoryFeatureGenerator {
	String refactorFileLabel = "refactored";
	String unrefactorFileLabel = "unrefactored";
	String lineBreak = "\n";
	long refactoredGroupCnt = 0, unrefactoredGroupCnt = 0;
	String cloneDataPath = null, oldArffFilePath = null;
	String tmpArffFilePath = null, newArffFilePath = null;
	String tmpNorArffFilePath = null, newNorArffFilePath = null;
		
	String projectPath = null, filteredLogFilePath = null, totalLogFilePath = null;
	ArrayList<String> filteredCommitList;
	ArrayList<String> totalCommitList;

	int totalAuthorNum = 0, totalCommitNum = 0;
	String repoFolderPath = null;
	Git git;
	Repository repo;

	public HistoryFeatureGenerator(String cloneDataPath, String oldArffFilePath, String projectPath,
			String filteredLogFilePath, String totalLogFilePath, String repoFolderPath) {
		this.cloneDataPath = cloneDataPath;
		this.oldArffFilePath = oldArffFilePath;
		int dotIndex = oldArffFilePath.lastIndexOf(".");
		this.newArffFilePath = oldArffFilePath.substring(0, dotIndex) + "His" + oldArffFilePath.substring(dotIndex);
		this.tmpArffFilePath = oldArffFilePath.substring(0, oldArffFilePath.lastIndexOf(GlobalSettings.pathSep) + 1)
				+ "tmpHis" + oldArffFilePath.substring(dotIndex);
		this.newNorArffFilePath = oldArffFilePath.substring(0, dotIndex) + "Norhis"
				+ oldArffFilePath.substring(dotIndex);
		this.tmpNorArffFilePath = oldArffFilePath.substring(0, oldArffFilePath.lastIndexOf(GlobalSettings.pathSep) + 1)
				+ "tmpNorhis" + oldArffFilePath.substring(dotIndex);
		this.projectPath = projectPath;
		this.filteredLogFilePath = filteredLogFilePath;
		this.totalLogFilePath = totalLogFilePath;
		this.repoFolderPath = repoFolderPath;		

		filteredCommitList = new ArrayList<String>();
		totalCommitList = new ArrayList<String>();
		setCommitList(this.filteredLogFilePath, filteredCommitList);
		setCommitList(this.totalLogFilePath, totalCommitList);
		this.initStudyRangeInfo();

		System.out.println("TotalCommitNum:" + totalCommitNum);
		System.out.println("TotalAuthorNum:" + totalAuthorNum);

		File gitFile = new File(projectPath + GlobalSettings.pathSep + ".git");
		try {
			git = Git.open(gitFile);
			repo = git.getRepository();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setCommitList(String logFilePath, ArrayList<String> commitList) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(logFilePath));
			String line = null;
			while ((line = in.readLine()) != null) {
				commitList.add(line);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initStudyRangeInfo() {
		HashSet<String> authorSet = new HashSet<String>();
		for (String commitStr : this.totalCommitList) {
			if (!commitStr.contains(","))
				continue;
			String[] tmp = commitStr.split(",");
			if (tmp.length >= 5) {
				this.totalCommitNum++;
				authorSet.add(tmp[2]);
			}
		}
		System.out.println("TotalAuthorSet:" + authorSet);
		this.totalAuthorNum = authorSet.size();
	}

	public String getNewArffFilePath() {
		return this.newArffFilePath;
	}

	public String getNormalizedNewArffFilePath() {
		return this.newNorArffFilePath;
	}

	private void outputCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(new Date()));
	}

	public void computeHistoryFeature() {
		try {
			File tmpHisArffFile = new File(this.tmpArffFilePath);
			if (!tmpHisArffFile.exists())
				tmpHisArffFile.createNewFile();
			PrintWriter pw = new PrintWriter(tmpHisArffFile);

			File tmpNorHisArffFile = new File(this.tmpNorArffFilePath);
			if (!tmpNorHisArffFile.exists())
				tmpNorHisArffFile.createNewFile();
			PrintWriter pwNor = new PrintWriter(tmpNorHisArffFile);

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
				processSingleCloneFile(cloneFile, unrefactorFileLabel, pw, pwNor);
			}
			System.out.print("afterProcessingUnRefactorClone:");
			outputCurrentTime();

			for (File cloneFile : refactorCloneFileList) {
				processSingleCloneFile(cloneFile, refactorFileLabel, pw, pwNor);
			}
			System.out.print("afterProcessingRefactorClone:");
			outputCurrentTime();
			pw.close();
			pwNor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSingleCloneFile(File cloneFile, String cloneFileLabel, PrintWriter pw, PrintWriter pwNor) {
		Vector<RefactoredInstance> refactoredInsList = new Vector<RefactoredInstance>();
		List<MyCloneClass> unrefactoredCloneList = new ArrayList<MyCloneClass>();

		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cloneFile));
			if (cloneFileLabel.equals(refactorFileLabel))
				refactoredInsList = (Vector<RefactoredInstance>) ois.readObject();
			else
				unrefactoredCloneList = (List<MyCloneClass>) ois.readObject();
			if (cloneFileLabel.equals(refactorFileLabel)) {
				for (RefactoredInstance refactoredIns : refactoredInsList) {
					Vector<MyFragment> frags = refactoredIns.getFragments();
					computeSingleCloneGroup(frags, cloneFileLabel, pw, pwNor, refactoredIns);
					System.out.print("AfterGettingOneRefactorInsFeature:");
					this.outputCurrentTime();
				}
			} else {
				for (MyCloneClass clazz : unrefactoredCloneList) {
					List<MyFragment> frags = clazz.getFragments();
					computeSingleCloneGroup(frags, cloneFileLabel, pw, pwNor, null);
					System.out.print("AfterGettingOneUnrefactorInsFeature:");
					this.outputCurrentTime();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void computeSingleCloneGroup(List<MyFragment> frags, String cloneFileLabel, PrintWriter pw,
			PrintWriter pwNor, RefactoredInstance refactoredIns) {
		// compute un/refactorGroupCount
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
		// compute history feature for each instance
		ArrayList<InstanceHistoryFeature> insHisFeatureList = new ArrayList<InstanceHistoryFeature>();
		ArrayList<ArrayList<Integer>> groupFileChangeRecorder = new ArrayList<ArrayList<Integer>>();
		for (MyFragment frag : frags) {
			InstanceHistoryFeature insHisFeature = this.buildSingleInsFeature(frag.getVersionRepoName(),
					frag.getFilePath(), refactoredIns);
			insHisFeatureList.add(insHisFeature);
			groupFileChangeRecorder.add(insHisFeature.fileChangeRecorder);
		}
		// compute group history feature, add it to each instance, print the
		// first ins's feature
		GroupHistoryFeature groupHisFeature = new GroupHistoryFeature(frags, groupFileChangeRecorder);

		for (InstanceHistoryFeature insHisFeature : insHisFeatureList) {
			insHisFeature.groupHisFeature = groupHisFeature;
			pw.print(newCloneFileLabel + "," + insHisFeature.toString());
			pw.println();
			pwNor.print(newCloneFileLabel + "," + insHisFeature.norToString(totalCommitNum, totalAuthorNum));
			pwNor.println();
			// Write one feature for a clone group
			break;
		}
		pw.flush();
		pwNor.flush();
	}

	private InstanceHistoryFeature buildSingleInsFeature(String repoFolderName, String cloneFilePath,
			RefactoredInstance refactoredIns) {
		int numOfCommitFileExist = 0;
		HashSet<String> authorSet = new HashSet<String>();
		ArrayList<Integer> changeRecorder = new ArrayList<Integer>();
		ArrayList<Integer> changeRecorderForSamePkg = new ArrayList<Integer>();

		String curHash;
		curHash = this.getHashStrFromFilterLog(repoFolderName);
		//NewRefactorCommitLocator locator = new NewRefactorCommitLocator(this.projectPath, this.filteredLogFilePath,
		//		this.totalLogFilePath, this.repoFolderPath);
		//locator.setHashvalueList();
		//if (refactoredIns != null) {
			//curHash = locator.getRealChangeID(refactoredIns, GlobalSettings.simi);
		//}
		int isCloneFileChanged, isFileInSamePkgAsCloneChanged;
		String clonePkgPath = cloneFilePath.substring(0, cloneFilePath.lastIndexOf(GlobalSettings.pathSep));
		int hisCommitNum = 0;
		while (true) {
			if (hisCommitNum >= this.totalCommitNum * GlobalSettings.historyRatio)
				break;
			String parentHash = null, author = null;
			isCloneFileChanged = 0;
			isFileInSamePkgAsCloneChanged = 0;
			if (this.isCloneFileExist(curHash, cloneFilePath)) {
				numOfCommitFileExist++;
				RevWalk walk = new RevWalk(repo);
				ObjectId objId;
				try {
					objId = repo.resolve(curHash);
					RevCommit revCommit = walk.parseCommit(objId);
					if (revCommit.getParentCount() == 0) {
						isCloneFileChanged = 1;
						isFileInSamePkgAsCloneChanged = 1;
						changeRecorder.add(isCloneFileChanged);
						changeRecorderForSamePkg.add(isFileInSamePkgAsCloneChanged);
						break;
					}
					parentHash = revCommit.getParent(0).getName();
					author = revCommit.getAuthorIdent().getName();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (this.isFileChanged(curHash, parentHash, cloneFilePath)) {
					authorSet.add(author);
					isCloneFileChanged = 1;
				}
				if (this.isFileInGivenPkgChanged(curHash, parentHash, clonePkgPath))
					isFileInSamePkgAsCloneChanged = 1;
			}else
				break;
			changeRecorder.add(isCloneFileChanged);
			changeRecorderForSamePkg.add(isFileInSamePkgAsCloneChanged);
			// update
			curHash = parentHash;
			hisCommitNum++;
		}
		InstanceHistoryFeature insHisFeature = new InstanceHistoryFeature(numOfCommitFileExist, authorSet.size(),
				changeRecorder, changeRecorderForSamePkg);
		return insHisFeature;
	}

	private String getHashStrFromFilterLog(String repoFolderName) {
		int which = filteredCommitList.size() - Integer.parseInt(repoFolderName);
		String commitStr = filteredCommitList.get(which);
		String[] tmp = commitStr.split(",");
		if (tmp.length >= 5)
			return tmp[0];
		return null;
	}

	/*
	 * ToDo(Maybe):is parent hash str in study range
	 */
	private boolean isInStudyCommitRange(String hashStr) {
		return false;
	}

	private boolean isCloneFileExist(String hashStr, String cloneFilePath) {
		RevWalk walk = new RevWalk(repo);
		ObjectId objId;
		try {
			// System.out.println("HashStr:"+hashStr);
			objId = repo.resolve(hashStr);
			RevCommit revCommit = walk.parseCommit(objId);
			RevTree revTree = revCommit.getTree();
			TreeWalk treeWalk = TreeWalk.forPath(repo, this.getRepoRelativeFilePath(cloneFilePath), revTree);
			String cloneFilePathInGitRepo = this.projectPath + GlobalSettings.pathSep
					+ this.getRepoRelativeFilePath(cloneFilePath);
			if (treeWalk == null) {
				// System.out.println("Unexist!!!!! hash:" + hashStr + "
				// CloneFilePath:" + cloneFilePathInGitRepo);
				return false;
			} else {
				// System.out.println("Exist~~~~~hash:" + hashStr + "
				// CloneFilePath:" + cloneFilePathInGitRepo);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getRepoRelativeFilePath(String cloneFilePath) {
//		System.out.println("repoFolderPath:"+this.repoFolderPath);
//		System.out.println("cloneFilePath:"+cloneFilePath);

		String filePathWithoutRepoFolder = cloneFilePath.substring(this.repoFolderPath.length());
		String[] tmp = filePathWithoutRepoFolder.split(GlobalSettings.pathSep);
		int relativePathStart = tmp[0].length() + tmp[1].length() + 2;
		//System.out.println(cloneFilePath);
		//System.out.println(filePathWithoutRepoFolder.substring(relativePathStart));
		return filePathWithoutRepoFolder.substring(relativePathStart);
	}

	private List<DiffEntry> getDiffList(String curHash, String parentHash) {
		try {
			RevWalk walk = new RevWalk(repo);
			ObjectId curCommitId = repo.resolve(curHash);
			ObjectId preCommitId = repo.resolve(parentHash);
			ObjectId curTreeId = walk.parseCommit(curCommitId).getTree().getId();
			ObjectId preTreeId = walk.parseCommit(preCommitId).getTree().getId();

			ObjectReader reader = repo.newObjectReader();
			// Create the tree iterator for each commit
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, preTreeId);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, curTreeId);

			List<DiffEntry> diffList = git.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter).call();
			return diffList;
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isFileChanged(String curHash, String parentHash, String cloneFilePath) {
		List<DiffEntry> diffList = this.getDiffList(curHash, parentHash);
		for (DiffEntry diff : diffList) {
			if (diff.getNewPath().equals(diff.getOldPath()) && cloneFilePath.contains(diff.getNewPath())) {
				return true;
			}
		}
		return false;
	}

	private boolean isFileInGivenPkgChanged(String curHash, String parentHash, String clonePkgPath) {
		List<DiffEntry> diffList = this.getDiffList(curHash, parentHash);
		for (DiffEntry diff : diffList) {
			if (diff.getNewPath().equals(diff.getOldPath())) {
				String diffFilePath = diff.getNewPath();
				if (!diffFilePath.contains(GlobalSettings.pathSep))
					continue;
				String diffPkgPath = diffFilePath.substring(0, diffFilePath.lastIndexOf(GlobalSettings.pathSep));
				if (clonePkgPath.contains(diffPkgPath)) {
					return true;
				}
			}
		}
		return false;
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

	private void combineHistoryFeature(String oldArffFilePath, String tmpArffFilePath, String newArffFilePath) {
		try {
			File oldArffFile = new File(oldArffFilePath);
			File tmpArffFile = new File(tmpArffFilePath);
			BufferedReader brOld = new BufferedReader(new FileReader(oldArffFile));
			BufferedReader brTmp = new BufferedReader(new FileReader(tmpArffFile));
			File newArffFile = new File(newArffFilePath);
			if (!newArffFile.exists())
				newArffFile.createNewFile();
			PrintWriter pw = new PrintWriter(new FileWriter(newArffFile));
			String featureStr = null;
			boolean afterData = false;
			while ((featureStr = brOld.readLine()) != null) {
				// System.out.println("featureStr:"+featureStr);
				if (!afterData) {
					if (featureStr.startsWith("@attribute cloneEval {refactored, unrefactored}")) {
						pw.println("@attribute numOfCommitFileExist real");
						pw.println("@attribute numFileDistinctAuthor real");
						pw.println("@attribute numOfCommitFileChange real");
						pw.println("@attribute numOfCommitRecentFileChange real");
						pw.println("@attribute numOfCommitFileOfSamePkgChange real");
						pw.println("@attribute numOfCommitRecentFileOfSamePkgChange real");
						// Group History Feature
						pw.println("@attribute numOfCommitAllFragsChange real");
						pw.println("@attribute numOfCommitNoFragChange real");
						pw.println("@attribute numOfCommit1FragChange real");
						pw.println("@attribute numOfCommit2FragsChange real");
						pw.println("@attribute numOfCommit3FragsChange real");
						pw.println(featureStr);
						pw.println(brOld.readLine());
						pw.println(brOld.readLine());
						afterData = true;
					} else
						pw.println(featureStr);
				} else {
					String hisFeatureStr = brTmp.readLine();
					String hisFeature = hisFeatureStr.substring(hisFeatureStr.indexOf(","));

					String oldFeatureWithoutLabel = featureStr.substring(0, featureStr.lastIndexOf(","));
					String label = featureStr.substring(featureStr.lastIndexOf(","));

					String newFeatureStr = oldFeatureWithoutLabel + hisFeature + label;
					pw.println(newFeatureStr);
				}
			}
			pw.close();
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void combineHistoryFeature() {
		combineHistoryFeature(this.oldArffFilePath, this.tmpArffFilePath, this.newArffFilePath);
		combineHistoryFeature(this.oldArffFilePath, this.tmpNorArffFilePath, this.newNorArffFilePath);
	}
}
