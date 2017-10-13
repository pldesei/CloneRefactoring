This clone method is located in File: 
solr/contrib/dataimporthandler/src/java/org/apache/solr/handler/dataimport/FileDataSource.java
The line range of this clone method is: 85-93
The content of this clone method is as follows:
  public Reader getData(String query) {
    File f = getFile(basePath,query);
    try {
      return openStream(f);
    } catch (Exception e) {
      wrapAndThrow(SEVERE,e,"Unable to open File : "+f.getAbsolutePath());
      return null;
    }
  }
