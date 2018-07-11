        public final IRubyObject fromNative(ThreadContext context, int value) {
            long address = ((long) value) & PointerResultConverter.ADDRESS_MASK;
            if (address == 0) {
                return context.getRuntime().getNil();
            }
            int len = (int) IO.getStringLength(address);
            if (len == 0) {
                return RubyString.newEmptyString(context.getRuntime());
            }
            byte[] bytes = new byte[len];
            IO.getByteArray(address, bytes, 0, len);

            RubyString s =  RubyString.newStringShared(context.getRuntime(), bytes);
            s.setTaint(true);
            return s;
        }
