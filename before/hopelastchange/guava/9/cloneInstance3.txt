This clone instance is located in File: 
d/guava-tests/test/com/google/common/collect/ImmutableBiMapTest.java
The line range of this clone instance is: 105-126
The content of this clone instance is as follows:
    @Override protected void assertMoreInvariants(Map<K, V> map) {

      BiMap<K, V> bimap = (BiMap<K, V>) map;

      for (Entry<K, V> entry : map.entrySet()) {
        assertEquals(entry.getKey() + "=" + entry.getValue(),
            entry.toString());
        assertEquals(entry.getKey(), bimap.inverse().get(entry.getValue()));
      }

      assertEquals("{" + joiner.join(map.entrySet()) + "}",
          map.toString());
      assertEquals("[" + joiner.join(map.entrySet()) + "]",
          map.entrySet().toString());
      assertEquals("[" + joiner.join(map.keySet()) + "]",
          map.keySet().toString());
      assertEquals("[" + joiner.join(map.values()) + "]",
          map.values().toString());

      assertEquals(Sets.newHashSet(map.entrySet()), map.entrySet());
      assertEquals(Sets.newHashSet(map.keySet()), map.keySet());
    }
