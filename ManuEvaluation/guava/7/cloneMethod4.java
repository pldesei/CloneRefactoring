This clone method is located in File: 
android/guava/src/com/google/common/collect/Multimaps.java
The line range of this clone method is: 378-384
The content of this clone method is as follows:
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      factory = (Supplier<? extends SortedSet<V>>) stream.readObject();
      valueComparator = factory.get().comparator();
      Map<K, Collection<V>> map = (Map<K, Collection<V>>) stream.readObject();
      setMap(map);
    }
