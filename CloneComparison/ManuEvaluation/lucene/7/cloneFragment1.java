This clone fragment is located in File: 
solr/core/src/test/org/apache/solr/highlight/TestPostingsSolrHighlighter.java
The line range of this clone fragment is: 127-133
The content of this clone fragment is as follows:
  public void testTags() {
    assertQ("different pre/post tags",
        req("q", "text:document", "sort", "id asc", "hl", "true", "hl.tag.pre", "[", "hl.tag.post", "]"),
        "count(//lst[@name='highlighting']/*)=2",
        "//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='[document] one'",
        "//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second [document]'");
  }
