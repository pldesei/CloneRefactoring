This clone instance is located in File: 
core/src/main/java/org/elasticsearch/index/fielddata/ScriptDocValues.java
The line range of this clone instance is: 139-146
The content of this clone instance is as follows:
        public ReadableDateTime getDate() throws IOException {
            deprecationLogger.deprecated("getDate on numeric fields is deprecated. Use a date field to get dates.");
            if (dates == null) {
                dates = new Dates(in);
                dates.setNextDocId(docId);
            }
            return dates.getValue();
        }
