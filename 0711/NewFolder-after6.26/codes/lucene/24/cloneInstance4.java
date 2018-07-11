  public void testLongMissingFacet() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1")); // missing
    assertU(adoc("id", "2", "longdv", "-1"));
    assertU(adoc("id", "3", "longdv", "4"));
    assertU(commit());
    assertQ(req("q", "*:*", "facet", "true", "facet.field", "longdv", "facet.mincount", "1", "facet.missing", "true"),
        "//lst[@name='facet_fields']/lst[@name='longdv']/int[@name='-1'][.=1]",
        "//lst[@name='facet_fields']/lst[@name='longdv']/int[@name='4'][.=1]",
        "//lst[@name='facet_fields']/lst[@name='longdv']/int[.=2]");
  }
