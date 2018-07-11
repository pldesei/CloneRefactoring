    public void testCreateWithReader3() throws IOException {
        String tempDir = System.getProperty("tempDir");
        if (tempDir == null)
            throw new IOException("java.io.tmpdir undefined, cannot run test");

        String dirName = tempDir + "/lucenetestindexwriter";
        try {

          // add one document & close writer
          IndexWriter writer = new IndexWriter(dirName, new WhitespaceAnalyzer(), true, IndexWriter.MaxFieldLength.LIMITED);
          addDoc(writer);
          writer.close();

          // now open reader:
          IndexReader reader = IndexReader.open(dirName);
          assertEquals("should be one document", reader.numDocs(), 1);

          // now open index for create:
          writer = new IndexWriter(dirName, new WhitespaceAnalyzer(), true, IndexWriter.MaxFieldLength.LIMITED);
          assertEquals("should be zero documents", writer.docCount(), 0);
          addDoc(writer);
          writer.close();

          assertEquals("should be one document", reader.numDocs(), 1);
          IndexReader reader2 = IndexReader.open(dirName);
          assertEquals("should be one document", reader2.numDocs(), 1);
          reader.close();
          reader2.close();
        } finally {
          rmDir(new File(dirName));
        }
    }
