This clone method is located in File: org.eclipse.jdt.core/search/org/eclipse/jdt/internal/core/nd/field/FieldSearchIndex.java
The line range of this clone method is: 270-282
The content of this clone method is as follows:
	public List<T> findAll(final Nd nd, long address, final SearchCriteria searchCriteria){
		final List<T> result = new ArrayList<T>();
		get(nd, address).accept(new SearchCriteriaToBtreeVisitorAdapter(searchCriteria,nd) {
			@SuppressWarnings("unchecked")
			@Override
			protected boolean acceptResult(long resultAddress) {
				result.add((T)NdNode.load(nd, resultAddress));
				return true;
			}
		});
		return result;
	}
