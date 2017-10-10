(startLine=85 endLine=93 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/solr/contrib/dataimporthandler/src/java/org/apache/solr/handler/dataimport/FileDataSource.java)
  public Reader getData(String query) {
    File f = getFile(basePath,query);
    try {
      return openStream(f);
    } catch (Exception e) {
      wrapAndThrow(SEVERE,e,"Unable to open File : "+f.getAbsolutePath());
      return null;
    }
  }
