This clone fragment is located in File: 
core/src/main/java/org/elasticsearch/search/aggregations/support/ValuesSource.java
The line range of this clone fragment is: 314-326
The content of this clone fragment is as follows:
                public boolean advanceExact(int target) throws IOException {
                    if (doubleValues.advanceExact(target)) {
                        resize(doubleValues.docValueCount());
                        script.setDocument(target);
                        for (int i = 0; i < docValueCount(); ++i) {
                            script.setNextAggregationValue(doubleValues.nextValue());
                            values[i] = script.runAsDouble();
                        }
                        sort();
                        return true;
                    }
                    return false;
                }
