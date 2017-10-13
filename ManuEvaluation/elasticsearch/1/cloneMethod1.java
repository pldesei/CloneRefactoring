This clone method is located in File: 
core/src/main/java/org/apache/lucene/search/grouping/CollapsingDocValuesSource.java
The line range of this clone method is: 109-120
The content of this clone method is as follows:
                            public boolean advanceExact(int target) throws IOException {
                                if (sorted.advanceExact(target)) {
                                    if (sorted.docValueCount() > 1) {
                                        throw new IllegalStateException("failed to collapse " + target +
                                            ", the collapse field must be single valued");
                                    }
                                    value = sorted.nextValue();
                                    return true;
                                } else {
                                    return false;
                                }
                            }
