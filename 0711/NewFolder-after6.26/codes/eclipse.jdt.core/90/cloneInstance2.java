		if (dimensions != 0 && dimensions <= parameterizedQualifiedTypeReference.dimensions()) {
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
