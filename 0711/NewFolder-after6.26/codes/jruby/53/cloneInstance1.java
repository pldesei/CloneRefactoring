    public static RubyArray members(IRubyObject recv, Block block) {
        RubyArray member = (RubyArray) getInternalVariable((RubyClass) recv, "__member__");

        assert !member.isNil() : "uninitialized struct";

        RubyArray result = recv.getRuntime().newArray(member.getLength());
        for (int i = 0,k=member.getLength(); i < k; i++) {
            // this looks weird, but it's because they're RubySymbol and that's java.lang.String internally
            result.append(recv.getRuntime().newString(member.eltInternal(i).asJavaString()));
        }

        return result;
    }
