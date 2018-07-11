protected void consumeMarkerAnnotation() {
	// MarkerAnnotation ::= '@' Name
	MarkerAnnotation markerAnnotation = null;
	int length = this.identifierLengthStack[this.identifierLengthPtr--];
	TypeReference typeReference;
	if (length == 1) {
		typeReference = new SingleTypeReference(
				this.identifierStack[this.identifierPtr], 
				this.identifierPositionStack[this.identifierPtr--]);
	} else {
		char[][] tokens = new char[length][];
		this.identifierPtr -= length;
		long[] positions = new long[length];
		System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
		System.arraycopy(
			this.identifierPositionStack, 
			this.identifierPtr + 1, 
			positions, 
			0, 
			length);
		typeReference = new QualifiedTypeReference(tokens, positions);
	}
	markerAnnotation = new MarkerAnnotation(typeReference, this.intStack[this.intPtr--]);
	markerAnnotation.declarationSourceEnd = markerAnnotation.sourceEnd;
	pushOnExpressionStack(markerAnnotation);
	if(options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		this.problemReporter().invalidUsageOfAnnotation(markerAnnotation);
	}
}
