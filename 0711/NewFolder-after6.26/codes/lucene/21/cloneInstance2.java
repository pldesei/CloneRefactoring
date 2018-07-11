  public void testDoubleSortMissingLast() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1", "doubledv_missinglast", "-1.3"));
    assertU(adoc("id", "2", "doubledv_missinglast", "4.2"));
    assertU(commit());
    assertQ(req("q", "*:*", "sort", "doubledv_missinglast asc"),
        "//result/doc[1]/str[@name='id'][.=1]",
        "//result/doc[2]/str[@name='id'][.=2]",
        "//result/doc[3]/str[@name='id'][.=0]");
    assertQ(req("q", "*:*", "sort", "doubledv_missinglast desc"),
        "//result/doc[1]/str[@name='id'][.=2]",
        "//result/doc[2]/str[@name='id'][.=1]",
        "//result/doc[3]/str[@name='id'][.=0]");
  }
