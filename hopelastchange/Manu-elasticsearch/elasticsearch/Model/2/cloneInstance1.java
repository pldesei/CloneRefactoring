(startLine=112 endLine=123 srcPath=/root/Projects/newestVersion/elasticsearch/00001/elasticsearch/core/src/main/java/org/elasticsearch/search/aggregations/bucket/sampler/DiversifiedMapSamplerAggregator.java)
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
