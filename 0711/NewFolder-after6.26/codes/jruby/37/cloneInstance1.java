    public static IRubyObject convertJavaToUsableRubyObject(Ruby runtime, Object object) {
        if (object == null) return runtime.getNil();
        
        // if it's already IRubyObject, don't re-wrap (JRUBY-2480)
        if (object instanceof IRubyObject) {
            return (IRubyObject)object;
        }
        
        if (object instanceof InternalJavaProxy) {
            InternalJavaProxy internalJavaProxy = (InternalJavaProxy) object;
            IRubyObject orig = internalJavaProxy.___getInvocationHandler().getOrig();

            if (orig != null) {
                return orig;
            }
        }

        JavaConverter converter = JAVA_CONVERTERS.get(object.getClass());
        if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
            return Java.getInstance(runtime, object);
        }
        return converter.convert(runtime, object);
    }
