This clone fragment is located in File: 
android/guava-tests/test/com/google/common/collect/ImmutableSortedMultisetTest.java
The line range of this clone fragment is: 486-493
The content of this clone fragment is as follows:
  public void testAsList() {
    ImmutableSortedMultiset<String> multiset = ImmutableSortedMultiset.of("a", "a", "b", "b", "b");
    ImmutableList<String> list = multiset.asList();
    assertEquals(ImmutableList.of("a", "a", "b", "b", "b"), list);
    SerializableTester.reserializeAndAssert(list);
    assertEquals(2, list.indexOf("b"));
    assertEquals(4, list.lastIndexOf("b"));
  }