(startLine=217 endLine=222 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/solr/solrj/src/java/org/apache/solr/client/solrj/request/CollectionAdminRequest.java)
    public SolrParams getParams() {
      ModifiableSolrParams params = new ModifiableSolrParams(super.getParams());
      params.set(CoreAdminParams.COLLECTION, collection);
      params.set(CoreAdminParams.SHARD, shard);
      return params;
    }
