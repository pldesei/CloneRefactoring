  public void testSingleNonOptimizedSegment() throws Exception {
    Directory dir = new RAMDirectory();
    
    IndexWriterConfig conf = newIndexWriterConfig(TEST_VERSION_CURRENT, null);
    conf.setMergePolicy(NoMergePolicy.COMPOUND_FILES);
    IndexWriter writer = new IndexWriter(dir, conf);
    
    addDocs(writer, 3);
    addDocs(writer, 5);
    addDocs(writer, 3);
    
    writer.close();
  
    // delete the last document, so that the last segment is optimized.
    IndexReader r = IndexReader.open(dir, false);
    r.deleteDocument(r.numDocs() - 1);
    r.close();
    
    conf = newIndexWriterConfig(TEST_VERSION_CURRENT, null);
    LogMergePolicy lmp = new LogDocMergePolicy();
    lmp.setMaxMergeDocs(3);
    conf.setMergePolicy(lmp);
    
    writer = new IndexWriter(dir, conf);
    writer.optimize();
    writer.close();
    
    // Verify that the last segment does not have deletions.
    SegmentInfos sis = new SegmentInfos();
    sis.read(dir);
    assertEquals(3, sis.size());
    assertFalse(sis.info(2).hasDeletions());
  }
