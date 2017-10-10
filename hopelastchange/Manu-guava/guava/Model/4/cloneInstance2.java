(startLine=563 endLine=578 srcPath=/home/ubuntu/newestVersion/guava/1/guava/android/guava/src/com/google/common/collect/Multisets.java)
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
