  public void testLongSortMissingFirst() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1", "longdv_missingfirst", "-1"));
    assertU(adoc("id", "2", "longdv_missingfirst", "4"));
    assertU(commit());
    assertQ(req("q", "*:*", "sort", "longdv_missingfirst asc"),
        "//result/doc[1]/str[@name='id'][.=0]",
        "//result/doc[2]/str[@name='id'][.=1]",
        "//result/doc[3]/str[@name='id'][.=2]");
    assertQ(req("q", "*:*", "sort", "longdv_missingfirst desc"),
        "//result/doc[1]/str[@name='id'][.=0]",
        "//result/doc[2]/str[@name='id'][.=2]",
        "//result/doc[3]/str[@name='id'][.=1]");
  }
