(startLine=831 endLine=845 srcPath=/home/ubuntu/newestVersion/guava/1/guava/android/guava/src/com/google/common/collect/Sets.java)
      public UnmodifiableIterator<E> iterator() {
        return new AbstractIterator<E>(){
          final Iterator<E> itr = set1.iterator();
          @Override
          protected E computeNext() {
            while (itr.hasNext()) {
              E e = itr.next();
              if (!set2.contains(e)) {
                return e;
              }
            }
            return endOfData();
          }
        };
      }
