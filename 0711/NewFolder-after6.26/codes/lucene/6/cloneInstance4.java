  public void inform(ResourceLoader loader) {
    String wordFiles = args.get("words");
    ignoreCase = getBoolean("ignoreCase", false);
    if (wordFiles != null) {
      try {
        List<String> files = StrUtils.splitFileNames(wordFiles);
        if (words == null && files.size() > 0){
          words = new CharArraySet(files.size() * 10, ignoreCase);
        }
        for (String file : files) {
          List<String> wlist = loader.getLines(file.trim());
          //TODO: once StopFilter.makeStopSet(List) method is available, switch to using that so we can avoid a toArray() call
          words.addAll(StopFilter.makeStopSet((String[]) wlist.toArray(new String[0]), ignoreCase));
        }
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
