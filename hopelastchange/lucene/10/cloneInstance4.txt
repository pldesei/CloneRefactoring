This clone instance is located in File: 
solr/core/src/java/org/apache/solr/search/HashDocSet.java
The line range of this clone instance is: 266-290
The content of this clone instance is as follows:
  public DocSet union(DocSet other) {
   if (other instanceof HashDocSet) {
     // set "a" to the smallest doc set
     final HashDocSet a = size()<=other.size() ? this : (HashDocSet)other;
     final HashDocSet b = size()<=other.size() ? (HashDocSet)other : this;

     int[] result = new int[a.size()+b.size()];
     int resultCount=0;
     // iterate over the largest table first, adding w/o checking.
     for (int i=0; i<b.table.length; i++) {
       int id=b.table[i];
       if (id>=0) result[resultCount++]=id;
     }

     // now iterate over smaller set, adding all not already in larger set.
     for (int i=0; i<a.table.length; i++) {
       int id=a.table[i];
       if (id>=0 && !b.exists(id)) result[resultCount++]=id;
     }

     return new HashDocSet(result,0,resultCount);
   } else {
     return other.union(this);
   }
  }
