(startLine=96 endLine=101 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/lucene/core/src/java/org/apache/lucene/document/SortedSetDocValuesRangeQuery.java)
  public Query rewrite(IndexReader reader) throws IOException {
    if (lowerValue == null && upperValue == null) {
      return new FieldValueQuery(field);
    }
    return super.rewrite(reader);
  }
