(startLine=139 endLine=146 srcPath=/root/Projects/newestVersion/elasticsearch/00001/elasticsearch/core/src/main/java/org/elasticsearch/index/fielddata/ScriptDocValues.java)
        public ReadableDateTime getDate() throws IOException {
            deprecationLogger.deprecated("getDate on numeric fields is deprecated. Use a date field to get dates.");
            if (dates == null) {
                dates = new Dates(in);
                dates.setNextDocId(docId);
            }
            return dates.getValue();
        }
