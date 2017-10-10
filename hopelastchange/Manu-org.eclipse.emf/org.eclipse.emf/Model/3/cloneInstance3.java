(startLine=596 endLine=635 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.common/src/org/eclipse/emf/common/util/WeakInterningHashSet.java)
  public E get(E object)
  {
    if (internalQueue != null)
    {
      cleanup();
    }

    if (object == null)
    {
      // Whether null is present or not, we always return null.
      //
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
            // Return the already present value.
            //
            return otherObject;
          }
        }
      }

      // Return null when becase the value isn't in the set.
      //
      return null;
    }
  }
