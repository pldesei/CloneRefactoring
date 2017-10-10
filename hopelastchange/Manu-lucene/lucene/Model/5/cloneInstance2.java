(startLine=85 endLine=90 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/lucene/core/src/java/org/apache/lucene/document/SortedNumericDocValuesRangeQuery.java)
  public Query rewrite(IndexReader reader) throws IOException {
    if (lowerValue == Long.MIN_VALUE && upperValue == Long.MAX_VALUE) {
      return new FieldValueQuery(field);
    }
    return super.rewrite(reader);
  }
