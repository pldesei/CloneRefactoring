  public void testNumDocsLimit() throws Exception {
    // tests that the max merge docs constraint is applied during optimize.
    Directory dir = new RAMDirectory();

    // Prepare an index w/ several small segments and a large one.
    IndexWriterConfig conf = newIndexWriterConfig(TEST_VERSION_CURRENT, null);
    // prevent any merges from happening.
    conf.setMergePolicy(NoMergePolicy.COMPOUND_FILES);
    IndexWriter writer = new IndexWriter(dir, conf);

    addDocs(writer, 3);
    addDocs(writer, 3);
    addDocs(writer, 5);
    addDocs(writer, 3);
    addDocs(writer, 3);
    addDocs(writer, 3);
    addDocs(writer, 3);
    
    writer.close();

    conf = newIndexWriterConfig(TEST_VERSION_CURRENT, null);
    LogMergePolicy lmp = new LogDocMergePolicy();
    lmp.setMaxMergeDocs(3);
    conf.setMergePolicy(lmp);
    
    writer = new IndexWriter(dir, conf);
    writer.optimize();
    writer.close();

    // Should only be 3 segments in the index, because one of them exceeds the size limit
    SegmentInfos sis = new SegmentInfos();
    sis.read(dir);
    assertEquals(3, sis.size());
  }
