(startLine=270 endLine=282 srcPath=/root/Projects/newestVersion/eclipse.jdt.core/00001/eclipse.jdt.core/org.eclipse.jdt.core/search/org/eclipse/jdt/internal/core/nd/field/FieldSearchIndex.java)
	public List<T> findAll(final Nd nd, long address, final SearchCriteria searchCriteria) {
		final List<T> result = new ArrayList<T>();
		get(nd, address).accept(new SearchCriteriaToBtreeVisitorAdapter(searchCriteria, nd) {
			@SuppressWarnings("unchecked")
			@Override
			protected boolean acceptResult(long resultAddress) {
				result.add((T)NdNode.load(nd, resultAddress));
				return true;
			}
		});

		return result;
	}
