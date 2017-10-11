This clone fragment is located in File: 
lucene/core/src/test/org/apache/lucene/search/TestSimpleExplanations.java
The line range of this clone fragment is: 666-676
The content of this clone fragment is as follows:
  public void testMultiFieldBQofPQ5() throws Exception {
    BooleanQuery.Builder query = new BooleanQuery.Builder();

    PhraseQuery leftChild = new PhraseQuery(1, FIELD, "w3", "w2");
    query.add(leftChild, BooleanClause.Occur.SHOULD);

    PhraseQuery rightChild = new PhraseQuery(1, ALTFIELD, "w3", "w2");
    query.add(rightChild, BooleanClause.Occur.SHOULD);

    qtest(query.build(), new int[] { 1,3 });
  }
