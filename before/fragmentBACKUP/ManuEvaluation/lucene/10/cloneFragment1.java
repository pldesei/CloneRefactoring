This clone fragment is located in File: 
solr/core/src/java/org/apache/solr/search/HashDocSet.java
The line range of this clone fragment is: 164-194
The content of this clone fragment is as follows:
  public DocSet intersection(DocSet other) {
   if (other instanceof HashDocSet) {
     // set "a" to the smallest doc set for the most efficient
     // intersection.
     final HashDocSet a = size()<=other.size() ? this : (HashDocSet)other;
     final HashDocSet b = size()<=other.size() ? (HashDocSet)other : this;

     int[] result = new int[a.size()];
     int resultCount=0;
     for (int i=0; i<a.table.length; i++) {
       int id=a.table[i];
       if (id >= 0 && b.exists(id)) {
         result[resultCount++]=id;
       }
     }
     return new HashDocSet(result,0,resultCount);

   } else {

     int[] result = new int[size()];
     int resultCount=0;
     for (int i=0; i<table.length; i++) {
       int id=table[i];
       if (id >= 0 && other.exists(id)) {
         result[resultCount++]=id;
       }
     }
     return new HashDocSet(result,0,resultCount);
   }

  }
