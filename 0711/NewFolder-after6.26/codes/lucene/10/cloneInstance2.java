  public void testSingleNonOptimizedTooLargeSegment() throws Exception {
    Directory dir = new RAMDirectory();
    
    IndexWriterConfig conf = newIndexWriterConfig(TEST_VERSION_CURRENT, null);
    conf.setMergePolicy(NoMergePolicy.COMPOUND_FILES);
    IndexWriter writer = new IndexWriter(dir, conf);
    
    addDocs(writer, 5);
    
    writer.close();
  
    // delete the last document
    IndexReader r = IndexReader.open(dir, false);
    r.deleteDocument(r.numDocs() - 1);
    r.close();
    
    conf = newIndexWriterConfig(TEST_VERSION_CURRENT, null);
    LogMergePolicy lmp = new LogDocMergePolicy();
    lmp.setMaxMergeDocs(2);
    conf.setMergePolicy(lmp);
    
    writer = new IndexWriter(dir, conf);
    writer.optimize();
    writer.close();
    
    // Verify that the last segment does not have deletions.
    SegmentInfos sis = new SegmentInfos();
    sis.read(dir);
    assertEquals(1, sis.size());
    assertTrue(sis.info(0).hasDeletions());
  }
