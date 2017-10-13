This clone method is located in File: 
android/guava/src/com/google/common/collect/ImmutableMultimap.java
The line range of this clone method is: 552-558
The content of this clone method is as follows:
    public boolean contains(Object object) {
      if (object instanceof Entry) {
        Entry<?, ?> entry = (Entry<?, ?>) object;
        return multimap.containsEntry(entry.getKey(), entry.getValue());
      }
      return false;
    }
