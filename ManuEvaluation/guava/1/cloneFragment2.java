This clone fragment is located in File: 
android/guava/src/com/google/common/collect/Sets.java
The line range of this clone fragment is: 831-845
The content of this clone fragment is as follows:
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
