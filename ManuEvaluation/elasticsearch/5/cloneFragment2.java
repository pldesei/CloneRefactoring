This clone fragment is located in File: 
core/src/main/java/org/elasticsearch/index/fielddata/ScriptDocValues.java
The line range of this clone fragment is: 149-156
The content of this clone fragment is as follows:
        public List<ReadableDateTime> getDates() throws IOException {
            deprecationLogger.deprecated("getDates on numeric fields is deprecated. Use a date field to get dates.");
            if (dates == null) {
                dates = new Dates(in);
                dates.setNextDocId(docId);
            }
            return dates;
        }
