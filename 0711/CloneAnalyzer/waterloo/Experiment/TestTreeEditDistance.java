package waterloo.Experiment;

import java.io.File;
import java.util.List;
import java.util.Vector;

import distance.APTED;
import util.LblTree;

public class TestTreeEditDistance {
	
	private APTED ted;
	
	public static void main(String[] args) {
		TestTreeEditDistance t = new TestTreeEditDistance();
		Node node1 = t.solve("/Users/Sonia/guavaFilter/", "/Users/Sonia/guavaFilter/1/guava/android/bb/a.java");
		Node node2 = t.solve("/Users/Sonia/guavaFilter/", "/Users/Sonia/guavaFilter/1/guava/test/bb/b.java");
		String tree1 = t.transferExpr(node1);
		String tree2 = t.transferExpr(node2);
		System.out.println(tree1+"\n"+tree2);
		t.init();
		t.symmetricTest(tree1, tree2);

	}
	
	public void init() {
        ted= new APTED((float)1.0, (float)1.0, (float)1.0);
	}

	public double symmetricTest(String strT1, String strT2) {
        LblTree t1, t2;
        double result1, result2;
        t1 = LblTree.fromString(strT1);
        t2 = LblTree.fromString(strT2);
        result1 = ted.nonNormalizedTreeDist(t1, t2);
        //assertEquals(expected, result, 0.000000001);
        //System.out.println(result1);
        result2 = ted.nonNormalizedTreeDist(t2, t1);
        //assertEquals(expected, result, 0.000000001);
        //System.out.println(result2);
        int max = t1.getNodeCount() > t2.getNodeCount() ? t1.getNodeCount() : t2.getNodeCount();
        return (result1 + result2)/2/(t1.getNodeCount()+t2.getNodeCount());
        
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
		
	}
	
	public Node solve(String projectFilterPath, String filePath) {
		Node root = null;
		String rootPath = getRootPath(projectFilterPath, filePath);
		//System.out.println(rootPath);
		/*String leftPath = filePath.substring(rootPath.length());
		//System.out.println(leftPath);
		int depth = 1;
		while (leftPath.indexOf("/") != -1) {
			depth ++;
			leftPath = leftPath.substring(leftPath.indexOf("/")+1);
		}*/
		root = calcNode(rootPath, null);
		return root;
	}
	
	

	private Node calcNode(String rootPath, Node father) {
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

	private String getRootPath(String projectFilterPath, String filePath) {
		String ret = "";
		String tmp1 = "";
		if (projectFilterPath.lastIndexOf("/") == projectFilterPath.length() - 1)//"/XX/XX/XX/"
			tmp1 = filePath.substring(projectFilterPath.length());
		else 
			tmp1 = filePath.substring(projectFilterPath.length() + 1);//"10／guava／XX／XX／..."
		String tmp2 = tmp1.substring(tmp1.indexOf("/")+1);//"guava/XX/XX/..."
		String tmp3 = tmp2.substring(tmp2.indexOf("/")+1);//"XX/XX/..."
		String tmp4 = tmp3.substring(tmp3.indexOf("/")+1);//"XX/..."
		ret = filePath.substring(0, filePath.length()-tmp4.length()-1);
		return ret;
	}

}
