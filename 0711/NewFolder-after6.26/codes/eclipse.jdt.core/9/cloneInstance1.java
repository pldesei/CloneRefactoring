public void test021() {
	this.runConformTest(
		new String[] {
			"p1/Z.java",
			"package p1;	\n" +
			"public class Z extends AbstractA {	\n" +
			"	public static void main(String[] arguments) {	\n" +
			"		new Z().init(); 	\n" +
			"	}	\n" +
			"}	\n" +
			"abstract class AbstractB implements K {	\n" +
			"	public void init() {	\n" +
			"		System.out.println(\"AbstractB.init()\");	\n" +
			"	}	\n" +
			"}	\n" +
			"interface K {	\n" +
			"	void init();	\n" +
			"	void init(int i);	\n" +
			"}	\n",
			"p1/AbstractA.java",
			"package p1;	\n" +
			"public abstract class AbstractA extends AbstractB implements K {	\n" +
			"	public void init(int i) {	\n" +
			"	}	\n" +
			"}	\n"			
		},
		"AbstractB.init()"); // no special vm args			

		// check that "new Z().init()" is bound to "AbstractB.init()"
		final StringBuffer references = new StringBuffer(10);
		try {
			BinaryIndexer indexer = new BinaryIndexer(true);
			indexer.index(
				new IDocument() {
					public byte[] getByteContent() throws IOException {
						return Util.getFileByteContent(new File(OUTPUT_DIR + "/p1/Z.class"));
					}
					public char[] getCharContent() { return null; }
					public String getName() { return "Z.class"; }
					public String getStringContent() { return null; }
					public String getType() { return "class"; }
					public String getEncoding() { return ""; }

				}, 
				new IIndexerOutput() {
					public void addDocument(IDocument document) { 
						// do nothing
					}
					public void addRef(char[] word) { 
						references.append(word);
						references.append('\n');
					}
					public void addRef(String word) {
						//System.out.println(word);
					}
				});
		} catch(IOException e) {
			// ignore
		}
		String computedReferences = references.toString();
		boolean check = 
			computedReferences.indexOf("typeRef/AbstractB") >= 0
			&& computedReferences.indexOf("ref/p1") >= 0
			&& computedReferences.indexOf("ref/AbstractB") >= 0
			&& computedReferences.indexOf("methodRef/init/0") >= 0;
		if (!check){
			System.out.println(computedReferences);
		}
		assertTrue("did not bind 'new Z().init()' to AbstractB.init()'", check);
}
