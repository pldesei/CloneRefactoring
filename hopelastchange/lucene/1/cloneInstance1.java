This clone instance is located in File: 
solr/solrj/src/java/org/apache/solr/client/solrj/request/CollectionAdminRequest.java
The line range of this clone instance is: 217-222
The content of this clone instance is as follows:
    public SolrParams getParams() {
      ModifiableSolrParams params = new ModifiableSolrParams(super.getParams());
      params.set(CoreAdminParams.COLLECTION, collection);
      params.set(CoreAdminParams.SHARD, shard);
      return params;
    }
