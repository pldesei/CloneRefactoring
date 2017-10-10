(startLine=267 endLine=285 srcPath=/home/ubuntu/newestVersion/lucene/1/lucene/lucene/misc/src/java/org/apache/lucene/search/DocValuesStats.java)
    protected void doAccumulate(int count) throws IOException {
      int numValues = sndv.docValueCount();
      while (numValues-- > 0) {
        long val = sndv.nextValue();
        if (val > max) {
          max = val;
        }
        if (val < min) {
          min = val;
        }
        sum += val;
        double oldMean = mean;
        // for correct "running average computation", increase valuesCount with each value, rather than once before the
        // loop stats.
        ++valuesCount;
        mean += (val - mean) / valuesCount;
        variance += (val - mean) * (val - oldMean);
      }
    }
