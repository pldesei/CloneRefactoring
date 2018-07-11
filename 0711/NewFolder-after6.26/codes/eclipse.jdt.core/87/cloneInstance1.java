		BlockScope scope) {

			final int numberOfParens = (arrayQualifiedTypeReference.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT;
			if (numberOfParens > 0) {
				manageOpeningParenthesizedExpression(arrayQualifiedTypeReference, numberOfParens);
			}
			this.scribe.printArrayQualifiedReference(arrayQualifiedTypeReference.tokens.length, arrayQualifiedTypeReference.sourceEnd);
			int dimensions = getDimensions();
			if (dimensions != 0) {
				for (int i = 0; i < dimensions; i++) {
					this.scribe.printNextToken(TerminalTokens.TokenNameLBRACKET);
					this.scribe.printNextToken(TerminalTokens.TokenNameRBRACKET);
				}
			}
			if (numberOfParens > 0) {
				manageClosingParenthesizedExpression(arrayQualifiedTypeReference, numberOfParens);
			}
			return false;
	}
