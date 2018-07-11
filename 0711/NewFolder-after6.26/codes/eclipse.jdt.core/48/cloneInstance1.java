	public boolean visit(DoStatement node) {
		this.scribe.printNextToken(TerminalTokens.TokenNamedo);
		final int line = this.scribe.line;
		
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
				default:
					this.scribe.printNewLine();
					this.scribe.indent();
					action.accept(this);
					this.scribe.printNewLine();
					this.scribe.unIndent();
			}
		} else {
			this.scribe.indent();
			action.accept(this);
			this.scribe.unIndent();
		}
		
		if (this.preferences.insert_new_line_before_while_in_do_statement) {
			this.scribe.printNewLine();
		}
		this.scribe.printNextToken(TerminalTokens.TokenNamewhile, this.preferences.insert_space_after_closing_brace_in_block);
		this.scribe.printNextToken(TerminalTokens.TokenNameLPAREN, this.preferences.insert_space_before_opening_paren_in_while);
		
		if (this.preferences.insert_space_after_opening_paren_in_while) {
			this.scribe.space();
		}
		
		node.getExpression().accept(this);
		
		this.scribe.printNextToken(TerminalTokens.TokenNameRPAREN, this.preferences.insert_space_before_closing_paren_in_while);
		this.scribe.printNextToken(TerminalTokens.TokenNameSEMICOLON, this.preferences.insert_space_before_semicolon);
		this.scribe.printTrailingComment();
		return false;
	}
