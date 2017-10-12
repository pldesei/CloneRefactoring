This clone instance is located in File: plugins/org.eclipse.emf.common/src/org/eclipse/emf/common/util/WeakInterningHashSet.java
The line range of this clone instance is: 545-591
The content of this clone instance is as follows:
  public E intern(E object)
  {
    if (internalQueue != null)
    {
      cleanup();
    }

    if (object == null)
    {
      // The presence of null is encoded containsNull.
      //
      if (!containsNull)
      {
        containsNull = true;
        ++modCount;
        ++size;
      }
      return null;
    }
    else
    {
      // Iterate over the entries with the matching hash code.
      //
      int hashCode = hashCode(object);
      int index = index(hashCode, entries.length);
      for (Entry<E> entry = entries[index]; entry != null; entry = entry.next)
      {
        if (hashCode == entry.hashCode)
        {
          // Check that the referent isn't garbage collected and then compare it.
          //
          E otherObject = entry.get();
          if (object == otherObject || equals(object, otherObject))
          {
            // Return that already present value.
            //
            return otherObject;
          }
        }
      }

      // Add the entry because it's not already in the set, and return the value.
      //
      addEntry(index, newEntry(object, hashCode));
      return object;
    }
  }
