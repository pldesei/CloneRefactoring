This clone fragment is located in File: 
core/src/main/java/org/elasticsearch/index/fielddata/ScriptDocValues.java
The line range of this clone fragment is: 458-467
The content of this clone fragment is as follows:
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