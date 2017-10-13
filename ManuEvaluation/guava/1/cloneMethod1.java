This clone method is located in File: 
android/guava/src/com/google/common/collect/Sets.java
The line range of this clone method is: 769-784
The content of this clone method is as follows:
      public UnmodifiableIterator<E> iterator() {
        return new AbstractIterator<E>(){
          final Iterator<E> itr = set1.iterator();
          @Override
          protected E computeNext() {
            while (itr.hasNext()) {
              E e = itr.next();
              if (set2.contains(e)) {
                return e;
              }
            }
            return endOfData();
          }
        };
      }