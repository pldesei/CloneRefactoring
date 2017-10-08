This clone instance is located in File: 
d/guava/src/com/google/common/collect/ImmutableMultimap.java
The line range of this clone instance is: 552-558
The content of this clone instance is as follows:
    public boolean contains(Object object) {
      if (object instanceof Entry) {
        Entry<?, ?> entry = (Entry<?, ?>) object;
        return multimap.containsEntry(entry.getKey(), entry.getValue());
      }
      return false;
    }
