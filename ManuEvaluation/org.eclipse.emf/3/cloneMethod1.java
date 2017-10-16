This clone method is located in File: 
plugins/org.eclipse.emf.common/src/org/eclipse/emf/common/util/WeakInterningHashSet.java
The line range of this clone method is: 490-540
The content of this clone method is as follows:
  public boolean add(E object) {
    if (internalQueue != null) {
      cleanup();
    }
    if (object == null) {
      // The presence of null is encoded containsNull.
      if (!containsNull) {
        containsNull = true;
        ++modCount;
        ++size;
        return true;
      }
      else {
        return false;
      }
    }
    else {
      // Iterate over the entries with the matching hash code.
      //
      int hashCode = hashCode(object);
      int index = index(hashCode, entries.length);
      for (Entry<E> entry = entries[index]; entry != null; entry = entry.next)
      {
        if (hashCode == entry.hashCode) {
         //Check that the referent isn't garbage collected and then compare it.
          //
          E otherObject = entry.get();
          if (object == otherObject || equals(object, otherObject)) {
            // If it's present, return false;
            return false;
          }
        }
      }
     //Add the entry because it's not already in the set.
      addEntry(index, newEntry(object, hashCode));
      return true;
    }
  }
