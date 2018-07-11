        public final IRubyObject invoke(Ruby runtime, Function function, HeapInvocationBuffer args) {
            long address = invoker.invokeAddress(function, args);
            if (address == 0) {
                return runtime.getNil();
            }
            int len = (int) IO.getStringLength(address);
            if (len == 0) {
                return RubyString.newEmptyString(runtime);
            }
            byte[] bytes = new byte[len];
            IO.getByteArray(address, bytes, 0, len);
            
            RubyString s =  RubyString.newStringShared(runtime, bytes);
            s.setTaint(true);
            return s;
        }
