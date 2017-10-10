(startLine=338 endLine=348 srcPath=/root/Projects/newestVersion/elasticsearch/00001/elasticsearch/core/src/main/java/org/elasticsearch/index/fielddata/ScriptDocValues.java)
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
