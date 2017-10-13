This clone method is located in File: 
lucene/queries/src/java/org/apache/lucene/queries/function/valuesource/TFValueSource.java
The line range of this clone method is: 132-155
The content of this clone method is as follows:
      public float floatVal(int doc) {
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
            return similarity.tf(0);
          }

          // a match!
          return similarity.tf(docs.freq());
        } catch (IOException e) {
          throw new RuntimeException("caught exception in function "+description()+" : doc="+doc, e);
        }
      }