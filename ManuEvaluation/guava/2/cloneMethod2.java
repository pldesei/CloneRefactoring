This clone method is located in File: 
android/guava/src/com/google/common/collect/Multisets.java
The line range of this clone method is: 623-633
The content of this clone method is as follows:
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
