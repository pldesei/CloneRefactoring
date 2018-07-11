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
