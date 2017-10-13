This clone method is located in File: 
core/src/main/java/org/elasticsearch/search/aggregations/support/ValuesSource.java
The line range of this clone method is: 283-295
The content of this clone method is as follows:
                public boolean advanceExact(int target) throws IOException {
                    if (longValues.advanceExact(target)) {
                        resize(longValues.docValueCount());
                        script.setDocument(target);
                        for (int i = 0; i < docValueCount(); ++i) {
                            script.setNextAggregationValue(longValues.nextValue());
                            values[i] = script.runAsLong();
                        }
                        sort();
                        return true;
                    }
                    return false;
                }
