    private static final IRubyObject getStringParameter(Ruby runtime, Closure.Buffer buffer, int index) {
        long address = buffer.getAddress(index);
        if (address == 0) {
            return runtime.getNil();
        }
        int len = (int) IO.getStringLength(address);
        if (len == 0) {
            return RubyString.newEmptyString(runtime);
        }
        byte[] bytes = new byte[len];
        IO.getByteArray(address, bytes, 0, len);

        RubyString s = RubyString.newStringShared(runtime, bytes);
        s.setTaint(true);
        return s;
    }
