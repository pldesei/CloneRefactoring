    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
        JavaProxy proxy = castJavaProxy(self);

        int len = args.length;
        final Object[] convertedArgs;
        JavaConstructor constructor = (JavaConstructor) findCallable(self, name, args, len);
        if (constructor.isVarArgs()) {
            len = constructor.getArity() - 1;
            convertedArgs = new Object[len + 1];
            for (int i = 0; i < len && i < args.length; i++) {
                convertedArgs[i] = convertArg(args[i], constructor, i);
            }
            convertedArgs[len] = convertVarArgs(args, constructor);
        } else {
            convertedArgs = new Object[len];
            for (int i = 0; i < len && i < args.length; i++) {
                convertedArgs[i] = convertArg(args[i], constructor, i);
            }
        }

        proxy.setObject(constructor.newInstanceDirect(context, convertedArgs));
        
        return self;
    }
