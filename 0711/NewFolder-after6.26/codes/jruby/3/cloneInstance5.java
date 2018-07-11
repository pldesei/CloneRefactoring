    public RubyNumeric op_mod(RubyNumeric other) {
        if (other instanceof RubyFloat) {
            return RubyFloat.m_newFloat(getRuby(), getDoubleValue()).op_mod(other);
        } else {
            if (other instanceof RubyBignum) {
                return m_newBignum(getRuby(), getValue().mod(((RubyBignum)other).getValue()));
            } else {
                return m_newBignum(getRuby(), getValue().mod(BigInteger.valueOf(other.getLongValue())));
            }
        }
    }
