This clone instance is located in File: 
core/src/main/java/org/elasticsearch/index/fielddata/ScriptDocValues.java
The line range of this clone instance is: 376-383
The content of this clone instance is as follows:
        public double[] getLats() {
            List<GeoPoint> points = getValues();
            double[] lats = new double[points.size()];
            for (int i = 0; i < points.size(); i++) {
                lats[i] = points.get(i).lat();
            }
            return lats;
        }
