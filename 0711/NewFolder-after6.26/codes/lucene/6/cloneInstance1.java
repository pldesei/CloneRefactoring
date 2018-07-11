  public void inform(ResourceLoader loader) {
    String commonWordFiles = args.get("words");
    ignoreCase = getBoolean("ignoreCase", false);

    if (commonWordFiles != null) {
      try {
        List<String> files = StrUtils.splitFileNames(commonWordFiles);
        if (commonWords == null && files.size() > 0) {
          // default stopwords list has 35 or so words, but maybe don't make it
          // that big to start
          commonWords = new CharArraySet(files.size() * 10, ignoreCase);
        }
        for (String file : files) {
          List<String> wlist = loader.getLines(file.trim());
          // TODO: once StopFilter.makeStopSet(List) method is available, switch
          // to using that so we can avoid a toArray() call
          commonWords.addAll(CommonGramsFilter.makeCommonSet((String[]) wlist
              .toArray(new String[0]), ignoreCase));
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      commonWords = (CharArraySet) StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }
  }
