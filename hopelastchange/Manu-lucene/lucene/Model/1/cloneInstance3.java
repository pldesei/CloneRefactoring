(startLine=903 endLine=908 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/solr/solrj/src/java/org/apache/solr/client/solrj/request/CollectionAdminRequest.java)
    public SolrParams getParams() {
      ModifiableSolrParams params = (ModifiableSolrParams) super.getParams();
      params.set(CoreAdminParams.COLLECTION, collection);
      params.set(CoreAdminParams.COMMIT_NAME, commitName);
      return params;
    }
