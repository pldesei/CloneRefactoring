(startLine=385 endLine=392 srcPath=/root/Projects/newestVersion/elasticsearch/00001/elasticsearch/core/src/main/java/org/elasticsearch/index/fielddata/ScriptDocValues.java)
        public double[] getLons() {
            List<GeoPoint> points = getValues();
            double[] lons = new double[points.size()];
            for (int i = 0; i < points.size(); i++) {
                lons[i] = points.get(i).lon();
            }
            return lons;
        }
