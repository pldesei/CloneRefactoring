        public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
            Object[] newArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                IRubyObject arg = args[i];
                newArgs[i] = rubyToJava(arg);
            }

            try {
                Object result = (Object)handle.invoke(isStatic ? null : ((JavaObjectWrapper)self).object, newArgs);
                
                return javaToRuby(context.getRuntime(), result);
            } catch (Exception e) {
                if (context.getRuntime().getDebug().isTrue()) e.printStackTrace();
                throw context.getRuntime().newTypeError("Could not dispatch to " + className + "#" + methodName + " using " + prettySig);
            }
        }
