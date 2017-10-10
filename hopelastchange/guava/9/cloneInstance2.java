This clone instance is located in File: 
d/guava-tests/test/com/google/common/collect/ImmutableMapTest.java
The line range of this clone instance is: 167-185
The content of this clone instance is as follows:
    @Override protected void assertMoreInvariants(Map<K, V> map) {
      // TODO: can these be moved to MapInterfaceTest?
      for (Entry<K, V> entry : map.entrySet()) {
        assertEquals(entry.getKey() + "=" + entry.getValue(),
            entry.toString());
      }

      assertEquals("{" + joiner.join(map.entrySet()) + "}",
          map.toString());
      assertEquals("[" + joiner.join(map.entrySet()) + "]",
          map.entrySet().toString());
      assertEquals("[" + joiner.join(map.keySet()) + "]",
          map.keySet().toString());
      assertEquals("[" + joiner.join(map.values()) + "]",
          map.values().toString());

      assertEquals(MinimalSet.from(map.entrySet()), map.entrySet());
      assertEquals(Sets.newHashSet(map.keySet()), map.keySet());
    }
