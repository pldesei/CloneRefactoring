(startLine=552 endLine=558 srcPath=/home/ubuntu/newestVersion/guava/1/guava/android/guava/src/com/google/common/collect/ImmutableMultimap.java)
    public boolean contains(Object object) {
      if (object instanceof Entry) {
        Entry<?, ?> entry = (Entry<?, ?>) object;
        return multimap.containsEntry(entry.getKey(), entry.getValue());
      }
      return false;
    }
