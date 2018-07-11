    public RubyNumeric op_mul(RubyNumeric other) {
        if (other instanceof RubyFloat) {
            return RubyFloat.m_newFloat(getRuby(), getDoubleValue()).op_mul(other);
        } else {
            if (other instanceof RubyBignum) {
                return m_newBignum(getRuby(), getValue().multiply(((RubyBignum)other).getValue()));
            } else {
                return m_newBignum(getRuby(), getValue().multiply(BigInteger.valueOf(other.getLongValue())));
            }
        }
    }
