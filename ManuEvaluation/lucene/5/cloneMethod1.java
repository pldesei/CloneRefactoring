This clone method is located in File: 
lucene/core/src/java/org/apache/lucene/document/SortedSetDocValuesRangeQuery.java
The line range of this clone method is: 96-101
The content of this clone method is as follows:
  public Query rewrite(IndexReader reader) throws IOException {
    if (lowerValue == null && upperValue == null) {
      return new FieldValueQuery(field);
    }
    return super.rewrite(reader);
  }
