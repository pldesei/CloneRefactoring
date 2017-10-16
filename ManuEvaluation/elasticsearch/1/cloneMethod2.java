This clone method is located in File: 
core/src/main/java/org/apache/lucene/search/grouping/CollapsingDocValuesSource.java
The line range of this clone method is: 212-223
The content of this clone method is as follows:
	public boolean advanceExact(int target) throws IOException {
		if (sorted.advanceExact(target)) {
       	ord = (int) sorted.nextOrd();
			if (sorted.nextOrd() != SortedSetDocValues.NO_MORE_ORDS) {
				throw new IllegalStateException("failed to collapse " + target + ", the collapse field must be single valued");
			}
			return true;
		} else {
			return false;
		}
	}
