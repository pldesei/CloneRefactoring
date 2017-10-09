This clone instance is located in File: 
solr/core/src/test/org/apache/solr/highlight/TestUnifiedSolrHighlighter.java
The line range of this clone instance is: 203-209
The content of this clone instance is as follows:
  public void testUsingSimplePrePostTags() {
    assertQ("different pre/post tags",
        req("q", "text:document", "sort", "id asc", "hl", "true", "hl.simple.pre", "[", "hl.simple.post", "]"),
        "count(//lst[@name='highlighting']/*)=2",
        "//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='[document] one'",
        "//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second [document]'");
  }
