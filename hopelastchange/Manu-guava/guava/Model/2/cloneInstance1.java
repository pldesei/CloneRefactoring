(startLine=488 endLine=498 srcPath=/home/ubuntu/newestVersion/guava/1/guava/android/guava/src/com/google/common/collect/Multisets.java)
          protected Entry<E> computeNext() {
            while (iterator1.hasNext()) {
              Entry<E> entry1 = iterator1.next();
              E element = entry1.getElement();
              int count = Math.min(entry1.getCount(), multiset2.count(element));
              if (count > 0) {
                return immutableEntry(element, count);
              }
            }
            return endOfData();
          }
