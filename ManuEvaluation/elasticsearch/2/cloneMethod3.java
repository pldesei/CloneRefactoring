This clone method is located in File: 
core/src/main/java/org/elasticsearch/search/aggregations/bucket/sampler/DiversifiedNumericSamplerAggregator.java
The line range of this clone method is: 95-105
The content of this clone method is as follows:
                    public boolean advanceExact(int target) throws IOException {
                        if (values.advanceExact(target)) {
                            if (values.docValueCount() > 1) {
                                throw new IllegalArgumentException(
                                        "Sample diversifying key must be a single valued-field");
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
