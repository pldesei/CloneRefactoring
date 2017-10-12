This clone fragment is located in File: 
solr/core/src/java/org/apache/solr/search/HashDocSet.java
The line range of this clone fragment is: 197-223
The content of this clone fragment is as follows:
  public int intersectionSize(DocSet other) {
   if (other instanceof HashDocSet) {
     // set "a" to the smallest doc set for the most efficient
     // intersection.
     final HashDocSet a = size()<=other.size() ? this : (HashDocSet)other;
     final HashDocSet b = size()<=other.size() ? (HashDocSet)other : this;

     int resultCount=0;
     for (int i=0; i<a.table.length; i++) {
       int id=a.table[i];
       if (id >= 0 && b.exists(id)) {
         resultCount++;
       }
     }
     return resultCount;
   } else {
     int resultCount=0;
     for (int i=0; i<table.length; i++) {
       int id=table[i];
       if (id >= 0 && other.exists(id)) {
         resultCount++;
       }
     }
     return resultCount;
   }

  }
