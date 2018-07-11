    if (stopWordFiles != null) {
      try {
        List<String> files = StrUtils.splitFileNames(stopWordFiles);
          if (stopWords == null && files.size() > 0){
            //default stopwords list has 35 or so words, but maybe don't make it that big to start
            stopWords = new CharArraySet(files.size() * 10, ignoreCase);
          }
          for (String file : files) {
            List<String> wlist = loader.getLines(file.trim());
            stopWords.addAll(StopFilter.makeStopSet(wlist, ignoreCase));
          }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
