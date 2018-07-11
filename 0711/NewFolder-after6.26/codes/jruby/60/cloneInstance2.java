    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
        int len = args.length;
        final Object[] convertedArgs;
        JavaMethod method = (JavaMethod)findCallable(self, name, args, len);
        if (method.isVarArgs()) {
            len = method.getArity() - 1;
            convertedArgs = new Object[len + 1];
            for (int i = 0; i < len && i < args.length; i++) {
                convertedArgs[i] = convertArg(args[i], method, i);
            }
            convertedArgs[len] = convertVarArgs(args, method);
        } else {
            convertedArgs = new Object[len];
            for (int i = 0; i < len && i < args.length; i++) {
                convertedArgs[i] = convertArg(args[i], method, i);
            }
        }
        return method.invokeStaticDirect(context, convertedArgs);
    }
