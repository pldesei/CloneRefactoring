This clone fragment is located in File: org.eclipse.jdt.core/search/org/eclipse/jdt/internal/core/nd/field/FieldSearchIndex.java
The line range of this clone fragment is: 284-299
The content of this clone fragment is as follows:
	public List<T> findAll(final Nd nd, long address, final SearchCriteria searchCriteria, final int count) {
		final List<T> result = new ArrayList<T>();
		get(nd, address).accept(new SearchCriteriaToBtreeVisitorAdapter(searchCriteria, nd) {
			int remainingCount = count;
			@SuppressWarnings("unchecked")
			@Override
			protected boolean acceptResult(long resultAddress) {
				result.add((T)NdNode.load(nd, resultAddress));
				this.remainingCount--;
				return this.remainingCount > 0;
			}
		});
		return result;
	}
