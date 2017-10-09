This clone instance is located in File: 
d/guava/src/com/google/common/collect/Multisets.java
The line range of this clone instance is: 623-633
The content of this clone instance is as follows:
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
