(startLine=644 endLine=654 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/lucene/core/src/test/org/apache/lucene/search/TestSimpleExplanations.java)
  public void testMultiFieldBQofPQ3() throws Exception {
    BooleanQuery.Builder query = new BooleanQuery.Builder();

    PhraseQuery leftChild = new PhraseQuery(1, FIELD, "w1", "w2");
    query.add(leftChild, BooleanClause.Occur.SHOULD);

    PhraseQuery rightChild = new PhraseQuery(1, ALTFIELD, "w1", "w2");
    query.add(rightChild, BooleanClause.Occur.SHOULD);

    qtest(query.build(), new int[] { 0,1,2 });
  }
