This clone method is located in File: 
solr/core/src/java/org/apache/solr/search/HashDocSet.java
The line range of this clone method is: 226-249
The content of this clone method is as follows:
  public boolean intersects(DocSet other) {
   if (other instanceof HashDocSet) {
     // set "a" to the smallest doc set for the most efficient
     // intersection.
     final HashDocSet a = size()<=other.size() ? this : (HashDocSet)other;
     final HashDocSet b = size()<=other.size() ? (HashDocSet)other : this;

     for (int i=0; i<a.table.length; i++) {
       int id=a.table[i];
       if (id >= 0 && b.exists(id)) {
         return true;
       }
     }
     return false;
   } else {
     for (int i=0; i<table.length; i++) {
       int id=table[i];
       if (id >= 0 && other.exists(id)) {
         return true;
       }
     }
     return false;
   }
  }
