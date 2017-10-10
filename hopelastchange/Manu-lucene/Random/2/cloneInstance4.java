(startLine=211 endLine=217 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/solr/core/src/test/org/apache/solr/highlight/TestUnifiedSolrHighlighter.java)
  public void testUsingSimplePrePostTagsPerField() {
    assertQ("different pre/post tags",
        req("q", "text:document", "sort", "id asc", "hl", "true", "f.text.hl.simple.pre", "[", "f.text.hl.simple.post", "]"),
        "count(//lst[@name='highlighting']/*)=2",
        "//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='[document] one'",
        "//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second [document]'");
  }
