(startLine=458 endLine=467 srcPath=/root/Projects/newestVersion/elasticsearch/00001/elasticsearch/core/src/main/java/org/elasticsearch/index/fielddata/ScriptDocValues.java)
        public void setNextDocId(int docId) throws IOException {
            if (in.advanceExact(docId)) {
                resize(in.docValueCount());
                for (int i = 0; i < count; i++) {
                    values[i] = in.nextValue() == 1;
                }
            } else {
                resize(0);
            }
        }
