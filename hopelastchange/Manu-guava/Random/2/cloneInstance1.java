(startLine=143 endLine=148 srcPath=/home/ubuntu/newestVersion/guava/1/guava/android/guava/src/com/google/common/collect/Multimaps.java)
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      factory = (Supplier<? extends Collection<V>>) stream.readObject();
      Map<K, Collection<V>> map = (Map<K, Collection<V>>) stream.readObject();
      setMap(map);
    }
