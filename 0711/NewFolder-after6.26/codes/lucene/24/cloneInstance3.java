  public void testDoubleMissingFacet() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1")); // missing
    assertU(adoc("id", "2", "doubledv", "-1.3"));
    assertU(adoc("id", "3", "doubledv", "4.2"));
    assertU(commit());
    assertQ(req("q", "*:*", "facet", "true", "facet.field", "doubledv", "facet.mincount", "1", "facet.missing", "true"),
        "//lst[@name='facet_fields']/lst[@name='doubledv']/int[@name='-1.3'][.=1]",
        "//lst[@name='facet_fields']/lst[@name='doubledv']/int[@name='4.2'][.=1]",
        "//lst[@name='facet_fields']/lst[@name='doubledv']/int[.=2]");
  }
