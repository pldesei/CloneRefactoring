protected boolean matchIndexEntry() {
	if (parameterSimpleNames != null && parameterSimpleNames.length != decodedParameterCount) return false;

	if (selector != null) {
		switch(matchMode) {
			case EXACT_MATCH :
				return CharOperation.equals(selector, decodedSelector, isCaseSensitive);
			case PREFIX_MATCH :
				return CharOperation.prefixEquals(selector, decodedSelector, isCaseSensitive);
			case PATTERN_MATCH :
				return CharOperation.match(selector, decodedSelector, isCaseSensitive);
		}
	}
	return true;
}
