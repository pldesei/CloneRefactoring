    public static RubyArray members19(IRubyObject recv, Block block) {
        RubyArray member = (RubyArray) getInternalVariable((RubyClass) recv, "__member__");

        assert !member.isNil() : "uninitialized struct";

        RubyArray result = recv.getRuntime().newArray(member.getLength());
        for (int i = 0,k=member.getLength(); i < k; i++) {
            result.append(member.eltInternal(i));
        }

        return result;
    }
