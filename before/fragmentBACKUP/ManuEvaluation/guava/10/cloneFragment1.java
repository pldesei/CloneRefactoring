This clone fragment is located in File: 
android/guava-tests/test/com/google/common/collect/ImmutableMultisetTest.java
The line range of this clone fragment is: 491-498
The content of this clone fragment is as follows:
  public void testAsList() {
    ImmutableMultiset<String> multiset
        = ImmutableMultiset.of("a", "a", "b", "b", "b");
    ImmutableList<String> list = multiset.asList();
    assertEquals(ImmutableList.of("a", "a", "b", "b", "b"), list);
    assertEquals(2, list.indexOf("b"));
    assertEquals(4, list.lastIndexOf("b"));
  }