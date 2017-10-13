This clone method is located in File: 
solr/contrib/dataimporthandler/src/java/org/apache/solr/handler/dataimport/BinFileDataSource.java
The line range of this clone method is: 50-58
The content of this clone method is as follows:
  public InputStream getData(String query) {
    File f = FileDataSource.getFile(basePath,query);
    try {
      return new FileInputStream(f);
    } catch (FileNotFoundException e) {
      wrapAndThrow(SEVERE,e,"Unable to open file "+f.getAbsolutePath());
      return null;
    }
  }
