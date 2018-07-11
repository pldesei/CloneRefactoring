	public boolean visit(EnhancedForStatement node) {
		this.scribe.printNextToken(TerminalTokens.TokenNamefor);
	    final int line = this.scribe.line;
	    this.scribe.printNextToken(TerminalTokens.TokenNameLPAREN, this.preferences.insert_space_before_opening_paren_in_for);
		
		if (this.preferences.insert_space_after_opening_paren_in_for) {
			this.scribe.space();
		}
		node.getParameter().accept(this);

		this.scribe.printNextToken(TerminalTokens.TokenNameCOLON, this.preferences.insert_space_before_colon_in_for);
		if (this.preferences.insert_space_after_colon_in_for) {
			this.scribe.space();
		}
		node.getExpression().accept(this);

		this.scribe.printNextToken(TerminalTokens.TokenNameRPAREN, this.preferences.insert_space_before_closing_paren_in_for);
		
		final Statement action = node.getBody();
		if (action != null) {
			switch(action.getNodeType()) {
				case ASTNode.BLOCK :
		            formatLeftCurlyBrace(line, this.preferences.brace_position_for_block);
					action.accept(this);
					break;
				case ASTNode.EMPTY_STATEMENT :
					this.scribe.indent();
					action.accept(this);
					this.scribe.unIndent();
					break;
				default :
					this.scribe.indent();
					this.scribe.printNewLine();
					action.accept(this);
					this.scribe.unIndent();
			}
		} else {
			this.scribe.indent();
			action.accept(this);
			this.scribe.unIndent();
		}
		return false;
	}
