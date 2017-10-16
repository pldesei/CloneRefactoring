This clone method is located in File: 
android/guava/src/com/google/common/collect/Multisets.java
The line range of this clone method is: 392-449
The content of this clone method is as follows:
  public static <E> Multiset<E> union(
      final Multiset<? extends E> multiset1, final Multiset<? extends E> multiset2) {
    checkNotNull(multiset1);
    checkNotNull(multiset2);

    return new AbstractMultiset<E>() {
      @Override
      public boolean contains(@Nullable Object element) {
        return multiset1.contains(element) || multiset2.contains(element);
      }

      @Override
      public boolean isEmpty() {
        return multiset1.isEmpty() && multiset2.isEmpty();
      }

      @Override
      public int count(Object element) {
        return Math.max(multiset1.count(element), multiset2.count(element));
      }

      @Override
      Set<E> createElementSet() {
        return Sets.union(multiset1.elementSet(), multiset2.elementSet());
      }

      @Override
      Iterator<Entry<E>> entryIterator() {
        final Iterator<? extends Entry<? extends E>> iterator1 = multiset1.entrySet().iterator();
        final Iterator<? extends Entry<? extends E>> iterator2 = multiset2.entrySet().iterator();
        // TODO(lowasser): consider making the entries live views
        return new AbstractIterator<Entry<E>>() {
          @Override
          protected Entry<E> computeNext() {
            if (iterator1.hasNext()) {
              Entry<? extends E> entry1 = iterator1.next();
              E element = entry1.getElement();
              int count = Math.max(entry1.getCount(), multiset2.count(element));
              return immutableEntry(element, count);
            }
            while (iterator2.hasNext()) {
              Entry<? extends E> entry2 = iterator2.next();
              E element = entry2.getElement();
              if (!multiset1.contains(element)) {
                return immutableEntry(element, entry2.getCount());
              }
            }
            return endOfData();
          }
        };
      }

      @Override
      int distinctElements() {
        return elementSet().size();
      }
    };


  }
