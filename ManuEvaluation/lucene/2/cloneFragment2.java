This clone fragment is located in File: 
lucene/misc/src/java/org/apache/lucene/search/DocValuesStats.java
The line range of this clone fragment is: 304-322
The content of this clone fragment is as follows:
    protected void doAccumulate(int count) throws IOException {
      int numValues = sndv.docValueCount();
      while (numValues-- > 0) {
        double val = Double.longBitsToDouble(sndv.nextValue());
        if (Double.compare(val, max) > 0) {
          max = val;
        }
        if (Double.compare(val, min) < 0) {
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
