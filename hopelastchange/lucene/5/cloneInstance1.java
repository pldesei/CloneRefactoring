This clone instance is located in File: 
lucene/core/src/java/org/apache/lucene/document/SortedSetDocValuesRangeQuery.java
The line range of this clone instance is: 96-101
The content of this clone instance is as follows:
  public Query rewrite(IndexReader reader) throws IOException {
    if (lowerValue == null && upperValue == null) {
      return new FieldValueQuery(field);
    }
    return super.rewrite(reader);
  }
