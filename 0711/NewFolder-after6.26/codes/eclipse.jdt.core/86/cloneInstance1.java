		BlockScope scope) {

		final int numberOfParens = (arrayTypeReference.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT;
		if (numberOfParens > 0) {
			manageOpeningParenthesizedExpression(arrayTypeReference, numberOfParens);
		}
		this.scribe.printNextToken(SINGLETYPEREFERENCE_EXPECTEDTOKENS);

		int dimensions = getDimensions();
		if (dimensions != 0) {
			if (this.preferences.insert_space_before_opening_bracket_in_array_type_reference) {
				this.scribe.space();
			}
			for (int i = 0; i < dimensions; i++) {
				this.scribe.printNextToken(TerminalTokens.TokenNameLBRACKET);
				if (this.preferences.insert_space_between_brackets_in_array_type_reference) {
					this.scribe.space();
				}
				this.scribe.printNextToken(TerminalTokens.TokenNameRBRACKET);
			}
		}
		if (numberOfParens > 0) {
			manageClosingParenthesizedExpression(arrayTypeReference, numberOfParens);
		}
		return false;
	}
