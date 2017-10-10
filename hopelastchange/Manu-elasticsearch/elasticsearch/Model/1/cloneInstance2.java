(startLine=212 endLine=223 srcPath=/root/Projects/newestVersion/elasticsearch/00001/elasticsearch/core/src/main/java/org/apache/lucene/search/grouping/CollapsingDocValuesSource.java)
                            public boolean advanceExact(int target) throws IOException {
                                if (sorted.advanceExact(target)) {
                                    ord = (int) sorted.nextOrd();
                                    if (sorted.nextOrd() != SortedSetDocValues.NO_MORE_ORDS) {
                                        throw new IllegalStateException("failed to collapse " + target +
                                            ", the collapse field must be single valued");
                                    }
                                    return true;
                                } else {
                                    return false;
                                }
                            }
