    private int star(boolean spaceSeen) throws IOException {
        int c = src.read();
        
        switch (c) {
        case '*':
            if ((c = src.read()) == '=') {
                lex_state = LexState.EXPR_BEG;
                yaccValue = new Token("**", getPosition());
                return Tokens.tOP_ASGN;
            }
            src.unread(c);
            yaccValue = new Token("**", getPosition());
            c = Tokens.tPOW;
            break;
        case '=':
            lex_state = LexState.EXPR_BEG;
            yaccValue = new Token("*", getPosition());
            return Tokens.tOP_ASGN;
        default:
            src.unread(c);
            if ((lex_state == LexState.EXPR_ARG || lex_state == LexState.EXPR_CMDARG) && 
                    spaceSeen && !Character.isWhitespace(c)) {
                warnings.warning(ID.ARGUMENT_AS_PREFIX, getPosition(), "`*' interpreted as argument prefix", "*");
                c = Tokens.tSTAR;
            } else if (lex_state == LexState.EXPR_BEG || lex_state == LexState.EXPR_MID) {
                c = Tokens.tSTAR;
            } else {
                c = Tokens.tSTAR2;
            }
            yaccValue = new Token("*", getPosition());
        }
        
        determineExpressionState();
        return c;
    }
