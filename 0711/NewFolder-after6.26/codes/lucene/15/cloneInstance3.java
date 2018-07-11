        throws IOException {
      String field = entryKey.field;
      FieldCache.LongParser parser = (FieldCache.LongParser) entryKey.custom;
      if (parser == null) {
        try {
          return wrapper.getLongs(reader, field, DEFAULT_LONG_PARSER, setDocsWithField);
        } catch (NumberFormatException ne) {
          return wrapper.getLongs(reader, field, NUMERIC_UTILS_LONG_PARSER, setDocsWithField);
        }
      }
      final int maxDoc = reader.maxDoc();
      long[] retArray = null;

      Terms terms = reader.terms(field);
      FixedBitSet docsWithField = null;
      if (terms != null) {
        if (setDocsWithField) {
          final int termsDocCount = terms.getDocCount();
          assert termsDocCount <= maxDoc;
          if (termsDocCount == maxDoc) {
            // Fast case: all docs have this field:
            wrapper.setDocsWithField(reader, field, new Bits.MatchAllBits(maxDoc));
            setDocsWithField = false;
          }
        }
        final TermsEnum termsEnum = parser.termsEnum(terms);
        assert termsEnum != null : "TermsEnum must not be null";
        DocsEnum docs = null;
        while(true) {
          final BytesRef term = termsEnum.next();
          if (term == null) {
            break;
          }
          final long termval = parser.parseLong(term);
          if (retArray == null) {
            // late init so numeric fields don't double allocate
            retArray = new long[maxDoc];
          }

          docs = termsEnum.docs(null, docs, DocsEnum.FLAG_NONE);
          while (true) {
            final int docID = docs.nextDoc();
            if (docID == DocIdSetIterator.NO_MORE_DOCS) {
              break;
            }
            retArray[docID] = termval;
            if (setDocsWithField) {
              if (docsWithField == null) {
                // Lazy init
                docsWithField = new FixedBitSet(maxDoc);
              }
              docsWithField.set(docID);
            }
          }
        }
      }

      if (retArray == null) {
        // no values
        retArray = new long[maxDoc];
      }
      if (setDocsWithField) {
        wrapper.setDocsWithField(reader, field, docsWithField);
      }
      return retArray;
    }
