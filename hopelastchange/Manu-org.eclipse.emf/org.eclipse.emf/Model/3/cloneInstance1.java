(startLine=490 endLine=540 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.common/src/org/eclipse/emf/common/util/WeakInterningHashSet.java)
  public boolean add(E object)
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
        return true;
      }
      else
      {
        return false;
      }
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
            // If it's present, return false;
            //
            return false;
          }
        }
      }

      // Add the entry because it's not already in the set.
      //
      addEntry(index, newEntry(object, hashCode));
      return true;
    }
  }
