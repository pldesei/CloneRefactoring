    public RubyNumeric op_div(RubyNumeric other) {
        if (other instanceof RubyFloat) {
            return RubyFloat.m_newFloat(getRuby(), getDoubleValue()).op_div(other);
        } else {
            if (other instanceof RubyBignum) {
                return m_newBignum(getRuby(), getValue().divide(((RubyBignum)other).getValue()));
            } else {
                return m_newBignum(getRuby(), getValue().divide(BigInteger.valueOf(other.getLongValue())));
            }
        }
    }
