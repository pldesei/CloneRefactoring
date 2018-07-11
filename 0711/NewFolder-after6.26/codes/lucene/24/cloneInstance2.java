  public void testIntMissingFacet() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1")); // missing
    assertU(adoc("id", "2", "intdv", "-1"));
    assertU(adoc("id", "3", "intdv", "4"));
    assertU(commit());
    assertQ(req("q", "*:*", "facet", "true", "facet.field", "intdv", "facet.mincount", "1", "facet.missing", "true"),
        "//lst[@name='facet_fields']/lst[@name='intdv']/int[@name='-1'][.=1]",
        "//lst[@name='facet_fields']/lst[@name='intdv']/int[@name='4'][.=1]",
        "//lst[@name='facet_fields']/lst[@name='intdv']/int[.=2]");
  }
