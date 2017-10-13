This clone method is located in File: 
lucene/core/src/test/org/apache/lucene/search/TestSimpleExplanations.java
The line range of this clone method is: 655-665
The content of this clone method is as follows:
  public void testMultiFieldBQofPQ4() throws Exception {
    BooleanQuery.Builder query = new BooleanQuery.Builder();

    PhraseQuery leftChild = new PhraseQuery(1, FIELD, "w2", "w3");
    query.add(leftChild, BooleanClause.Occur.SHOULD);

    PhraseQuery rightChild = new PhraseQuery(1, ALTFIELD, "w2", "w3");
    query.add(rightChild, BooleanClause.Occur.SHOULD);

    qtest(query.build(), new int[] { 0,1,2,3 });
  }