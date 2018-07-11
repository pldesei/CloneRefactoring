  public void testDoubleSort() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1", "doubledv", "-1.3"));
    assertU(adoc("id", "2", "doubledv", "4.2"));
    assertU(commit());
    assertQ(req("q", "*:*", "sort", "doubledv asc"),
        "//result/doc[1]/str[@name='id'][.=1]",
        "//result/doc[2]/str[@name='id'][.=0]",
        "//result/doc[3]/str[@name='id'][.=2]");
    assertQ(req("q", "*:*", "sort", "doubledv desc"),
        "//result/doc[1]/str[@name='id'][.=2]",
        "//result/doc[2]/str[@name='id'][.=0]",
        "//result/doc[3]/str[@name='id'][.=1]");
  }
