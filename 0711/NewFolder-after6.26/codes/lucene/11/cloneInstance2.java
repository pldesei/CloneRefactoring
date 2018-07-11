  public StempelStemmer(InputStream stemmerTable) throws IOException {
    if (stemmerTable == null) return;
    
    DataInputStream in = new DataInputStream(new BufferedInputStream(
        stemmerTable));
    String method = in.readUTF().toUpperCase();
    if (method.indexOf('M') < 0) {
      stemmer = new org.egothor.stemmer.Trie(in);
    } else {
      stemmer = new org.egothor.stemmer.MultiTrie2(in);
    }
    in.close();
  }
