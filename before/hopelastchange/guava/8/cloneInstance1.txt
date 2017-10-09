This clone instance is located in File: 
d/guava/src/com/google/common/collect/ImmutableSetMultimap.java
The line range of this clone instance is: 451-457
The content of this clone instance is as follows:
    public boolean contains(@Nullable Object object) {
      if (object instanceof Entry) {
        Entry<?, ?> entry = (Entry<?, ?>) object;
        return multimap.containsEntry(entry.getKey(), entry.getValue());
      }
      return false;
    }
