  public void testStringSortMissingLast() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1", "stringdv_missinglast", "a"));
    assertU(adoc("id", "2", "stringdv_missinglast", "z"));
    assertU(commit());
    assertQ(req("q", "*:*", "sort", "stringdv_missinglast asc"),
        "//result/doc[1]/str[@name='id'][.=1]",
        "//result/doc[2]/str[@name='id'][.=2]",
        "//result/doc[3]/str[@name='id'][.=0]");
    assertQ(req("q", "*:*", "sort", "stringdv_missinglast desc"),
        "//result/doc[1]/str[@name='id'][.=2]",
        "//result/doc[2]/str[@name='id'][.=1]",
        "//result/doc[3]/str[@name='id'][.=0]");
  }
