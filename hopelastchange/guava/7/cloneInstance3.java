This clone instance is located in File: 
d/guava/src/com/google/common/collect/Multimaps.java
The line range of this clone instance is: 296-301
The content of this clone instance is as follows:
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      factory = (Supplier<? extends Set<V>>) stream.readObject();
      Map<K, Collection<V>> map = (Map<K, Collection<V>>) stream.readObject();
      setMap(map);
    }
