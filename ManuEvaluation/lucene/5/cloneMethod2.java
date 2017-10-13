This clone method is located in File: 
lucene/core/src/java/org/apache/lucene/document/SortedNumericDocValuesRangeQuery.java
The line range of this clone method is: 85-90
The content of this clone method is as follows:
  public Query rewrite(IndexReader reader) throws IOException {
    if (lowerValue == Long.MIN_VALUE && upperValue == Long.MAX_VALUE) {
      return new FieldValueQuery(field);
    }
    return super.rewrite(reader);
  }
