This clone method is located in File: 
android/guava/src/com/google/common/collect/Multimaps.java
The line range of this clone method is: 143-148
The content of this clone method is as follows:
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      factory = (Supplier<? extends Collection<V>>) stream.readObject();
      Map<K, Collection<V>> map = (Map<K, Collection<V>>) stream.readObject();
      setMap(map);
    }
