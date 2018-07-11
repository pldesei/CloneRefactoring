protected boolean matchIndexEntry() {
	if (parameterSimpleNames != null && parameterSimpleNames.length != decodedParameterCount) return false;

	if (declaringSimpleName != null) {
		switch(matchMode) {
			case EXACT_MATCH :
				return CharOperation.equals(declaringSimpleName, decodedTypeName, isCaseSensitive);
			case PREFIX_MATCH :
				return CharOperation.prefixEquals(declaringSimpleName, decodedTypeName, isCaseSensitive);
			case PATTERN_MATCH :
				return CharOperation.match(declaringSimpleName, decodedTypeName, isCaseSensitive);
		}
	}
	return true;
}
