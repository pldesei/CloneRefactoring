This clone fragment is located in File: 
solr/core/src/test/org/apache/solr/highlight/TestUnifiedSolrHighlighter.java
The line range of this clone fragment is: 211-217
The content of this clone fragment is as follows:
  public void testUsingSimplePrePostTagsPerField() {
    assertQ("different pre/post tags",
        req("q", "text:document", "sort", "id asc", "hl", "true", "f.text.hl.simple.pre", "[", "f.text.hl.simple.post", "]"),
        "count(//lst[@name='highlighting']/*)=2",
        "//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='[document] one'",
        "//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second [document]'");
  }
