    private int ampersand(boolean spaceSeen) throws IOException {
        int c = src.read();
        
        switch (c) {
        case '&':
            lex_state = LexState.EXPR_BEG;
            if ((c = src.read()) == '=') {
                yaccValue = new Token("&&", getPosition());
                lex_state = LexState.EXPR_BEG;
                return Tokens.tOP_ASGN;
            }
            src.unread(c);
            yaccValue = new Token("&&", getPosition());
            return Tokens.tANDOP;
        case '=':
            yaccValue = new Token("&", getPosition());
            lex_state = LexState.EXPR_BEG;
            return Tokens.tOP_ASGN;
        }
        src.unread(c);
        
        //tmpPosition is required because getPosition()'s side effects.
        //if the warning is generated, the getPosition() on line 954 (this line + 18) will create
        //a wrong position if the "inclusive" flag is not set.
        ISourcePosition tmpPosition = getPosition();
        if ((lex_state == LexState.EXPR_ARG || lex_state == LexState.EXPR_CMDARG) && 
                spaceSeen && !Character.isWhitespace(c)) {
            warnings.warning(ID.ARGUMENT_AS_PREFIX, tmpPosition, "`&' interpreted as argument prefix", "&");
            c = Tokens.tAMPER;
        } else if (lex_state == LexState.EXPR_BEG || 
                lex_state == LexState.EXPR_MID) {
            c = Tokens.tAMPER;
        } else {
            c = Tokens.tAMPER2;
        }
        
        determineExpressionState();
        
        yaccValue = new Token("&", tmpPosition);
        return c;
    }
