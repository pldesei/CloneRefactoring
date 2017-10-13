This clone method is located in File: 
android/guava/src/com/google/common/collect/Multisets.java
The line range of this clone method is: 619-635
The content of this clone method is as follows:
      Iterator<Entry<E>> entryIterator() {
        final Iterator<Entry<E>> iterator1 = multiset1.entrySet().iterator();
        return new AbstractIterator<Entry<E>>() {
          @Override
          protected Entry<E> computeNext() {
            while (iterator1.hasNext()) {
              Entry<E> entry1 = iterator1.next();
              E element = entry1.getElement();
              int count = entry1.getCount() - multiset2.count(element);
              if (count > 0) {
                return immutableEntry(element, count);
              }
            }
            return endOfData();
          }
        };
      }
