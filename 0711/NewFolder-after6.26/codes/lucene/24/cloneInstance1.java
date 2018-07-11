  public void testFloatMissingFacet() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1")); // missing
    assertU(adoc("id", "2", "floatdv", "-1.3"));
    assertU(adoc("id", "3", "floatdv", "4.2"));
    assertU(commit());
    assertQ(req("q", "*:*", "facet", "true", "facet.field", "floatdv", "facet.mincount", "1", "facet.missing", "true"),
        "//lst[@name='facet_fields']/lst[@name='floatdv']/int[@name='-1.3'][.=1]",
        "//lst[@name='facet_fields']/lst[@name='floatdv']/int[@name='4.2'][.=1]",
        "//lst[@name='facet_fields']/lst[@name='floatdv']/int[.=2]");
  }
