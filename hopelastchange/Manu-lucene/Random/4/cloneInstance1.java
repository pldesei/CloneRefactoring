(startLine=125 endLine=148 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/lucene/queries/src/java/org/apache/lucene/queries/function/valuesource/TermFreqValueSource.java)
      public int intVal(int doc) {
        try {
          if (doc < lastDocRequested) {
            // out-of-order access.... reset
            reset();
          }
          lastDocRequested = doc;

          if (atDoc < doc) {
            atDoc = docs.advance(doc);
          }

          if (atDoc > doc) {
            // term doesn't match this document... either because we hit the
            // end, or because the next doc is after this doc.
            return 0;
          }

          // a match!
          return docs.freq();
        } catch (IOException e) {
          throw new RuntimeException("caught exception in function "+description()+" : doc="+doc, e);
        }
      }
