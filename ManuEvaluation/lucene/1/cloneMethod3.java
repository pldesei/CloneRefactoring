This clone method is located in File: 
solr/solrj/src/java/org/apache/solr/client/solrj/request/CollectionAdminRequest.java
The line range of this clone method is: 903-908
The content of this clone method is as follows:
    public SolrParams getParams() {
      ModifiableSolrParams params = (ModifiableSolrParams) super.getParams();
      params.set(CoreAdminParams.COLLECTION, collection);
      params.set(CoreAdminParams.COMMIT_NAME, commitName);
      return params;
    }
