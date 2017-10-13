This clone method is located in File: 
solr/solrj/src/java/org/apache/solr/client/solrj/request/CollectionAdminRequest.java
The line range of this clone method is: 238-243
The content of this clone method is as follows:
    public SolrParams getParams() {
      ModifiableSolrParams params = new ModifiableSolrParams(super.getParams());
      params.set(CoreAdminParams.COLLECTION, collection);
      params.set(CoreAdminParams.SHARD, shard);
      return params;
    }
