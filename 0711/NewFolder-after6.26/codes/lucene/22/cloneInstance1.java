  public void testFloatMissingFunction() throws Exception {
    assertU(adoc("id", "0")); // missing
    assertU(adoc("id", "1", "floatdv", "-1.3"));
    assertU(adoc("id", "2", "floatdv", "4.2"));
    assertU(commit());
    assertQ(req("q", "*:*", "fl", "e:exists(floatdv)", "sort", "id asc"),
        "//result/doc[1]/bool[@name='e'][.='false']",
        "//result/doc[2]/bool[@name='e'][.='true']",
        "//result/doc[3]/bool[@name='e'][.='true']");
  }
