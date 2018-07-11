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
