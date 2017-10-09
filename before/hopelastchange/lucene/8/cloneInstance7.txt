This clone instance is located in File: 
lucene/core/src/test/org/apache/lucene/search/TestSimpleExplanations.java
The line range of this clone instance is: 688-698
The content of this clone instance is as follows:
  public void testMultiFieldBQofPQ7() throws Exception {
    BooleanQuery.Builder query = new BooleanQuery.Builder();

    PhraseQuery leftChild = new PhraseQuery(3, FIELD, "w3", "w2");
    query.add(leftChild, BooleanClause.Occur.SHOULD);

    PhraseQuery rightChild = new PhraseQuery(1, ALTFIELD, "w3", "w2");
    query.add(rightChild, BooleanClause.Occur.SHOULD);

    qtest(query.build(), new int[] { 0,1,2,3 });
  }
