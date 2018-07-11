  public void testLongMissingFunction() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1", "longdv", "-1"));
    assertU(adoc("id", "2", "longdv", "4"));
    assertU(commit());
    assertQ(req("q", "*:*", "fl", "e:exists(longdv)", "sort", "id asc"),
        "//result/doc[1]/bool[@name='e'][.='false']",
        "//result/doc[2]/bool[@name='e'][.='true']",
        "//result/doc[3]/bool[@name='e'][.='true']");
  }
