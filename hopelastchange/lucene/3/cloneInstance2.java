This clone instance is located in File: 
lucene/core/src/java/org/apache/lucene/index/MultiSorter.java
The line range of this clone instance is: 275-286
The content of this clone instance is as follows:
              public Comparable getComparable(int docID) throws IOException {
                assert docsInOrder(docID);
                int readerDocID = values.docID();
                if (readerDocID < docID) {
                  readerDocID = values.advance(docID);
                }
                if (readerDocID == docID) {
                  return reverseMul * (int) values.longValue();
                } else {
                  return reverseMul * missingValue;
                }
              }
