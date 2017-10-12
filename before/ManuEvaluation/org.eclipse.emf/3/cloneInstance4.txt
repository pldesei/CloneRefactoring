This clone instance is located in File: plugins/org.eclipse.emf.common/src/org/eclipse/emf/common/util/WeakInterningHashSet.java
The line range of this clone instance is: 641-690
The content of this clone instance is as follows:
  public boolean contains(Object object)
  {
    if (internalQueue != null)
    {
      cleanup();
    }

    if (object == null)
    {
      // The presence of null is encoded in the modCount.
      //
      return containsNull;
    }
    else
    {
      // First check if the object is of type E.
      //
      E instance = asInstance(object);
      if (instance == null)
      {
        return false;
      }
      else
      {
        // Iterate over the entries with the matching hash code.
        //
        int hashCode = hashCode(instance);
        int index = index(hashCode, entries.length);
        for (Entry<E> entry = entries[index]; entry != null; entry = entry.next)
        {
          if (hashCode == entry.hashCode)
          {
            // Check that the referent isn't garbage collected and then compare it.
            //
            E otherObject = entry.get();
            if (instance == otherObject || equals(instance, otherObject))
            {
              // Return true because it's present.
              //
              return true;
            }
          }
        }

        // Return false because it's not present.
        //
        return false;
      }
    }
  }
