        throws IOException {
      String field = entryKey.field;
      ShortParser parser = (ShortParser) entryKey.custom;
      if (parser == null) {
        return wrapper.getShorts(reader, field, FieldCache.DEFAULT_SHORT_PARSER, setDocsWithField);
      }
      final int maxDoc = reader.maxDoc();
      final short[] retArray = new short[maxDoc];
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
          final short termval = parser.parseShort(term);
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
      if (setDocsWithField) {
        wrapper.setDocsWithField(reader, field, docsWithField);
      }
      return retArray;
    }
