package waterloo;

import java.io.File;
import java.util.List;
import java.util.Vector;

import edu.pku.sei.codeclone.predictor.MyFragment;
import waterloo.Experiment.TestTreeEditDistance;

class Node {
	Node father;
	String name;
	List<Node> children = new Vector<Node>();
	
	public Node(Node fa, String name) {
		father = fa;
		this.name = name;
	}
	
	public void addChild(Node node) {
		children.add(node);
	}
	
	public List<Node> getChildren() {
		return children;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}

public class AddedGroupFeature {
	
	public List<MyFragment> frags;
	
	//new added
	public double treeDistance;
	
	public AddedGroupFeature(List<MyFragment> frags) {
		this.frags = frags;
		
		this.treeDistance = getTreeDistance();
	}
	
	public double getTreeDistance() {
		double treeSum = 0;
		for (int i = 0; i < this.frags.size(); i++) {
			for (int j = 0; j < this.frags.size(); j++) {
				if (i == j) continue;
				String filePath1 = frags.get(i).getFilePath();
				String filePath2 = frags.get(j).getFilePath();
				Vector<String> ret = getRootPath(filePath1, filePath2);
				if (ret == null ||ret.size() == 0) {
					//System.out.println("this no");
					treeSum += 1;
					continue;
				}
				String root1 = ret.elementAt(0);
				String root2 = ret.elementAt(1);
				Node node1 = calcNode(root1, null);
				Node node2 = calcNode(root2, null);
				node1.setName("same");
				node2.setName("same");
				TestTreeEditDistance t = new TestTreeEditDistance();
				t.init();
				treeSum += t.symmetricTest(transferExpr(node1), transferExpr(node2));

			}
		}
		
		
		return treeSum/(this.frags.size()*(this.frags.size()-1));
	}
	
	public String transferExpr(Node node) {
		String ret = "{" + node.getName();
		if (node.children.size() == 0) {
			ret += "}";
			return ret;
		}
		else {
			for (Node subnode: node.getChildren()) {
				ret += transferExpr(subnode);
			}
		}
		ret += "}";
		return ret;
	}
	
	private static Node calcNode(String rootPath, Node father) {
		File now = new File(rootPath);
		String name = rootPath.substring(rootPath.lastIndexOf("/") + 1);
		Node root = new Node(father, name);
		if (!now.isDirectory()) {
			return root;
		}
		File[] files = now.listFiles();
		for (File file : files) {
			Node node = calcNode(file.getAbsolutePath(), root);
			if (node != null) {
				root.addChild(node);
			}
		}
		return root;
	}
	
	public static Vector<String> getRootPath (String filePath1, String filePath2) {
		String[] dirs1 = filePath1.split("/");
		String[] dirs2 = filePath2.split("/");
		int max = dirs1.length > dirs2.length ? dirs1.length : dirs2.length;
		String root1 = "";
		String root2 = "";
		for (int m = 1; m < max-1; m ++) {
			int index1 = dirs1.length - 1 - m;
			int index2 = dirs2.length - 1 - m;
			if (index1 <= 0 || index2 <= 0)
				break;
			if (dirs1[index1].equals(dirs2[index2]))
				continue;
			else {
				for (int n = 1; n <= index1; n++) {
					root1 += "/" + dirs1[n];
				}
				for (int n = 1; n <= index2; n++) {
					root2 += "/" + dirs2[n];
				}
				break;
			}
		}
		if (root1.equals("") || root2.equals("")) {
			return null;
		}
		//System.out.println(root1 + "\n" + root2);
		Vector<String> ret = new Vector<String>();
		ret.addElement(root1);
		ret.addElement(root2);
		return ret;
	}
	/*
	public double getOldTreeDistance() {
		if (this.cloneInsFeatureList.size() <= 1)
			return 0;
		double treeSum = 0;
		for (int i = 0; i < this.cloneInsFeatureList.size(); i++) {
			for (int j = 0; j < this.cloneInsFeatureList.size(); j++) {
				TestTreeEditDistance t = new TestTreeEditDistance();
				t.init();
				treeSum += t.symmetricTest(this.cloneInsFeatureList.get(i).pathTree, this.cloneInsFeatureList.get(j).pathTree);
			}
		}
		return treeSum/(this.cloneInsFeatureList.size()*(this.cloneInsFeatureList.size()-1)/2);
	}
	*/
}
