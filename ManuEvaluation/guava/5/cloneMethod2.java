This clone method is located in File: 
android/guava/src/com/google/common/collect/Multisets.java
The line range of this clone method is: 558-580
The content of this clone method is as follows:
      Iterator<Entry<E>> entryIterator() {
        final Iterator<? extends Entry<? extends E>> iterator1 = multiset1.entrySet().iterator();
        final Iterator<? extends Entry<? extends E>> iterator2 = multiset2.entrySet().iterator();
        return new AbstractIterator<Entry<E>>() {
          @Override
          protected Entry<E> computeNext() {
            if (iterator1.hasNext()) {
              Entry<? extends E> entry1 = iterator1.next();
              E element = entry1.getElement();
              int count = entry1.getCount() + multiset2.count(element);
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
