(startLine=357 endLine=368 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/lucene/core/src/java/org/apache/lucene/index/MultiSorter.java)
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
