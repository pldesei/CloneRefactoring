(startLine=50 endLine=58 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/solr/contrib/dataimporthandler/src/java/org/apache/solr/handler/dataimport/BinFileDataSource.java)
  public InputStream getData(String query) {
    File f = FileDataSource.getFile(basePath,query);
    try {
      return new FileInputStream(f);
    } catch (FileNotFoundException e) {
      wrapAndThrow(SEVERE,e,"Unable to open file "+f.getAbsolutePath());
      return null;
    }
  }
