	public boolean visit(WhileStatement node) {
		this.scribe.printNextToken(TerminalTokens.TokenNamewhile);
		final int line = this.scribe.line;
		this.scribe.printNextToken(TerminalTokens.TokenNameLPAREN, this.preferences.insert_space_before_opening_paren_in_while);
		
		if (this.preferences.insert_space_after_opening_paren_in_while) {
			this.scribe.space();
		}
		node.getExpression().accept(this);
		
		this.scribe.printNextToken(TerminalTokens.TokenNameRPAREN, this.preferences.insert_space_before_closing_paren_in_while);
		
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
					this.scribe.printNewLine();
					this.scribe.indent();
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
