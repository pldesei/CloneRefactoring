(startLine=296 endLine=301 srcPath=/home/ubuntu/newestVersion/guava/1/guava/android/guava/src/com/google/common/collect/Multimaps.java)
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      factory = (Supplier<? extends Set<V>>) stream.readObject();
      Map<K, Collection<V>> map = (Map<K, Collection<V>>) stream.readObject();
      setMap(map);
    }
