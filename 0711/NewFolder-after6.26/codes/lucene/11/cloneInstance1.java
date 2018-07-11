      try {
        DataInputStream in = new DataInputStream(new BufferedInputStream(stream));
        String method = in.readUTF().toUpperCase();
        if (method.indexOf('M') < 0) {
          DEFAULT_TABLE = new org.egothor.stemmer.Trie(in);
        } else {
          DEFAULT_TABLE = new org.egothor.stemmer.MultiTrie2(in);
        }
        in.close();
      } catch (IOException ex) {
