# CloneRefactoring
package waterloo.Experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import edu.pku.sei.codeclone.predictor.MyCloneClass;
import edu.pku.sei.codeclone.predictor.MyFragment;
import waterloo.Util.GlobalSettings;

public class EmailAddressReader {
	Git git;
	Repository repo;
	String manuExpHash = null, manuExpRepoFolderPath = null;

	public EmailAddressReader(String projectPath, String manuExpHash, String manuExpRepoFolderPath) {
		File gitFile = new File(projectPath + GlobalSettings.pathSep + ".git");
		try {
			git = Git.open(gitFile);
			repo = git.getRepository();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.manuExpHash = manuExpHash;
		this.manuExpRepoFolderPath = manuExpRepoFolderPath;
	}

	public HashSet<String> getPreChangeAuthorEmails(MyCloneClass clazz) {
		HashSet<String> authorEmailSet = new HashSet<String>();
		for (MyFragment frag : clazz.getFragments()) {
			ArrayList<PersonIdent> singleCloneAuthorList = this.getPreChangeAuthors(frag.getFilePath());
			//System.out.println(singleCloneAuthorList.size());
			for (PersonIdent author : singleCloneAuthorList) {
				if (author.getEmailAddress() != null) {
					authorEmailSet.add(author.getEmailAddress());
					//System.out.println(author.getName()+ ":" + author.getEmailAddress());
				}
			}
		}
		return authorEmailSet;
	}

	private ArrayList<PersonIdent> getPreChangeAuthors(String cloneFilePath) {
		ArrayList<PersonIdent> authorList = new ArrayList<PersonIdent>();
		String curHash = this.manuExpHash;
		int hisCommitNum = 0;
		String clonePkgPath = cloneFilePath.substring(0, cloneFilePath.lastIndexOf(GlobalSettings.pathSep));
		while (true) {
			if (hisCommitNum >= 1000)
				break;
			String parentHash = null;
			PersonIdent author = null;
			
			if (this.isCloneFileExist(curHash, cloneFilePath)) {
				RevWalk walk = new RevWalk(repo);
				ObjectId objId;
				try {
					objId = repo.resolve(curHash);
					RevCommit revCommit = walk.parseCommit(objId);
					parentHash = revCommit.getParent(0).getName();
					author = revCommit.getAuthorIdent();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (author != null) {
				if (this.isFileChanged(curHash, parentHash, cloneFilePath) || 
						this.isFileInGivenPkgChanged(curHash, parentHash, clonePkgPath)) {
					authorList.add(author);
				}
				}
			} else
				break;
			curHash = parentHash;
			hisCommitNum++;
		}
		return authorList;
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
	
	private boolean isFileChanged(String curHash, String parentHash, String cloneFilePath) {
		List<DiffEntry> diffList = this.getDiffList(curHash, parentHash);
		for (DiffEntry diff : diffList) {
			if (diff.getNewPath().equals(diff.getOldPath()) && cloneFilePath.contains(diff.getNewPath())) {
				return true;
			}
		}
		return false;
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
	
	private boolean isCloneFileExist(String hashStr, String cloneFilePath) {
		RevWalk walk = new RevWalk(repo);
		ObjectId objId;
		try {
			objId = repo.resolve(hashStr);
			RevCommit revCommit = walk.parseCommit(objId);
			RevTree revTree = revCommit.getTree();
			// System.out.println("RelativeFilePath:"+this.getRepoRelativeFilePath(cloneFilePath));
			TreeWalk treeWalk = TreeWalk.forPath(repo, this.getRepoRelativeFilePath(cloneFilePath), revTree);
			if (treeWalk == null) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String getRepoRelativeFilePath(String cloneFilePath) {
		return cloneFilePath.substring(this.manuExpRepoFolderPath.length());
	}

	public static void main(String[] args) {
		//refactorCloneGroupList recommended by manuExp
		String projectPath="/root/Projects/org.eclipse.emf";
		String manuExpHash="39e89bbc44bb3c2535b713848961e07e0087d754";
		String manuExpRepoFolderPath="/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/";
		String groupPath="/root/Manus/Manu-org.eclipse.emf/groups.txt";
		List<MyCloneClass> refactorCloneGroupList=null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(groupPath)));
			refactorCloneGroupList = (List<MyCloneClass>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		EmailAddressReader emailReader=new EmailAddressReader(projectPath, manuExpHash,manuExpRepoFolderPath);
		HashSet<String> emailListAll = new HashSet<String>();
		int cnt = 0;
		System.out.println("Author List:");
		for(MyCloneClass refactorCloneGroup:refactorCloneGroupList){
			HashSet<String> emailList=emailReader.getPreChangeAuthorEmails(refactorCloneGroup);
			cnt ++;
			System.out.println("group"+cnt+":"+emailList.size());
			System.out.println(emailList);
			for (String str : emailList)
				emailListAll.add(str);
		}
		System.out.println("All:" + emailListAll.size() + "\n" + emailListAll);

	}

}
