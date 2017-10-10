(startLine=109 endLine=120 srcPath=/root/Projects/newestVersion/elasticsearch/00001/elasticsearch/core/src/main/java/org/apache/lucene/search/grouping/CollapsingDocValuesSource.java)
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
