This clone method is located in File: 
core/src/main/java/org/elasticsearch/index/fielddata/ScriptDocValues.java
The line range of this clone method is: 338-348
The content of this clone method is as follows:
        public void setNextDocId(int docId) throws IOException {
            if (in.advanceExact(docId)) {
                resize(in.docValueCount());
                for (int i = 0; i < count; i++) {
                    GeoPoint point = in.nextValue();
                    values[i].reset(point.lat(), point.lon());
                }
            } else {
                resize(0);
            }
        }
