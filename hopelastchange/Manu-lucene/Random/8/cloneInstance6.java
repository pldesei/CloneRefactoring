(startLine=677 endLine=687 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/lucene/core/src/test/org/apache/lucene/search/TestSimpleExplanations.java)
  public void testMultiFieldBQofPQ6() throws Exception {
    BooleanQuery.Builder query = new BooleanQuery.Builder();

    PhraseQuery leftChild = new PhraseQuery(2, FIELD, "w3", "w2");
    query.add(leftChild, BooleanClause.Occur.SHOULD);

    PhraseQuery rightChild = new PhraseQuery(2, ALTFIELD, "w3", "w2");
    query.add(rightChild, BooleanClause.Occur.SHOULD);

    qtest(query.build(), new int[] { 0,1,3 });
  }
