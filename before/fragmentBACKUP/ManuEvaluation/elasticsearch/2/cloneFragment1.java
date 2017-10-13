This clone fragment is located in File: 
core/src/main/java/org/elasticsearch/search/aggregations/bucket/sampler/DiversifiedMapSamplerAggregator.java
The line range of this clone fragment is: 112-123
The content of this clone fragment is as follows:
                    public boolean advanceExact(int target) throws IOException {
                        docID = target;
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
