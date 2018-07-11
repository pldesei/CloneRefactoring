    public RubyNumeric op_minus(RubyNumeric other) {
        if (other instanceof RubyFloat) {
            return RubyFloat.m_newFloat(getRuby(), getDoubleValue()).op_minus(other);
        } else {
            if (other instanceof RubyBignum) {
                return m_newBignum(getRuby(), getValue().subtract(((RubyBignum)other).getValue()));
            } else {
                return m_newBignum(getRuby(), getValue().subtract(BigInteger.valueOf(other.getLongValue())));
            }
        }
    }
