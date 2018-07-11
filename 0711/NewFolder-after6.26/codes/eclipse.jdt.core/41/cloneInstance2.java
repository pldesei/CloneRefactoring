protected void consumeSingleMemberAnnotation() {
	// SingleMemberAnnotation ::= '@' Name '(' MemberValue ')'
	SingleMemberAnnotation singleMemberAnnotation = null;
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
	singleMemberAnnotation = new SingleMemberAnnotation(typeReference, this.intStack[this.intPtr--]);
	singleMemberAnnotation.memberValue = this.expressionStack[this.expressionPtr--];
	this.expressionLengthPtr--;
	singleMemberAnnotation.declarationSourceEnd = this.rParenPos;
	pushOnExpressionStack(singleMemberAnnotation);
	if(options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		this.problemReporter().invalidUsageOfAnnotation(singleMemberAnnotation);
	}
}
