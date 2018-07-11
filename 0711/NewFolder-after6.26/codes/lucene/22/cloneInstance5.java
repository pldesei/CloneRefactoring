  public void testStringMissingFunction() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1", "stringdv", "a"));
    assertU(adoc("id", "2", "stringdv", "z"));
    assertU(commit());
    assertQ(req("q", "*:*", "fl", "e:exists(stringdv)", "sort", "id asc"),
        "//result/doc[1]/bool[@name='e'][.='false']",
        "//result/doc[2]/bool[@name='e'][.='true']",
        "//result/doc[3]/bool[@name='e'][.='true']");
  }
