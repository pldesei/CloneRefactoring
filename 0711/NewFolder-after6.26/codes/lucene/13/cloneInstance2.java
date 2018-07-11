  public void testRandomHugeStrings() throws Exception {
    Analyzer analyzer = new Analyzer() {

      @Override
      protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer tokenizer = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
        return new TokenStreamComponents(tokenizer, tokenizer);
      }

      @Override
      protected Reader initReader(Reader reader) {
        return new HTMLStripCharFilter(CharReader.get(reader));
      }
    };
    
    int numRounds = RANDOM_MULTIPLIER * 200;
    checkRandomData(random, analyzer, numRounds, 8192);
  }
