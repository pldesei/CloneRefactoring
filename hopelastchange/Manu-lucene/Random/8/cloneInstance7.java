(startLine=688 endLine=698 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/lucene/core/src/test/org/apache/lucene/search/TestSimpleExplanations.java)
  public void testMultiFieldBQofPQ7() throws Exception {
    BooleanQuery.Builder query = new BooleanQuery.Builder();

    PhraseQuery leftChild = new PhraseQuery(3, FIELD, "w3", "w2");
    query.add(leftChild, BooleanClause.Occur.SHOULD);

    PhraseQuery rightChild = new PhraseQuery(1, ALTFIELD, "w3", "w2");
    query.add(rightChild, BooleanClause.Occur.SHOULD);

    qtest(query.build(), new int[] { 0,1,2,3 });
  }
