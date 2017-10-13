This clone method is located in File: 
lucene/core/src/test/org/apache/lucene/search/TestSimpleExplanations.java
The line range of this clone method is: 633-643
The content of this clone method is as follows:
  public void testMultiFieldBQofPQ2() throws Exception {
    BooleanQuery.Builder query = new BooleanQuery.Builder();

    PhraseQuery leftChild = new PhraseQuery(FIELD, "w1", "w3");
    query.add(leftChild, BooleanClause.Occur.SHOULD);

    PhraseQuery rightChild = new PhraseQuery(ALTFIELD, "w1", "w3");
    query.add(rightChild, BooleanClause.Occur.SHOULD);

    qtest(query.build(), new int[] { 1,3 });
  }
