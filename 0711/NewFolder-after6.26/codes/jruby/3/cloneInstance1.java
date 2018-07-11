    public RubyNumeric op_plus(RubyNumeric other) {
        if (other instanceof RubyFloat) {
            return RubyFloat.m_newFloat(getRuby(), getDoubleValue()).op_plus(other);
        } else {
            if (other instanceof RubyBignum) {
                return m_newBignum(getRuby(), getValue().add(((RubyBignum)other).getValue()));
            } else {
                return m_newBignum(getRuby(), getValue().add(BigInteger.valueOf(other.getLongValue())));
            }
        }
    }
