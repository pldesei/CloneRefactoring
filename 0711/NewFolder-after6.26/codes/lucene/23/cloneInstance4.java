  public void testStringSortMissingFirst() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1", "stringdv_missingfirst", "a"));
    assertU(adoc("id", "2", "stringdv_missingfirst", "z"));
    assertU(commit());
    assertQ(req("q", "*:*", "sort", "stringdv_missingfirst asc"),
        "//result/doc[1]/str[@name='id'][.=0]",
        "//result/doc[2]/str[@name='id'][.=1]",
        "//result/doc[3]/str[@name='id'][.=2]");
    assertQ(req("q", "*:*", "sort", "stringdv_missingfirst desc"),
        "//result/doc[1]/str[@name='id'][.=0]",
        "//result/doc[2]/str[@name='id'][.=2]",
        "//result/doc[3]/str[@name='id'][.=1]");
  }
