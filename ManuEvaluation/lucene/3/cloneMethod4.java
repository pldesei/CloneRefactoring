This clone method is located in File: 
lucene/core/src/java/org/apache/lucene/index/MultiSorter.java
The line range of this clone method is: 357-368
The content of this clone method is as follows:
              public Comparable getComparable(int docID) throws IOException {
                assert docsInOrder(docID);
                int readerDocID = values.docID();
                if (readerDocID < docID) {
                  readerDocID = values.advance(docID);
                }
                if (readerDocID == docID) {
                  return reverseMul * Float.intBitsToFloat((int) values.longValue());
                } else {
                  return reverseMul * missingValue;
                }
              }