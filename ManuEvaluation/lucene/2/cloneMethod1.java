This clone method is located in File: 
lucene/misc/src/java/org/apache/lucene/search/DocValuesStats.java
The line range of this clone method is: 267-285
The content of this clone method is as follows:
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