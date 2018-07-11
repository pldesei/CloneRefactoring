        public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
            try {
                Object result = (Object)handle.invoke(isStatic ? null : ((JavaObjectWrapper)self).object);
                
                return javaToRuby(context.getRuntime(), result);
            } catch (Exception e) {
                if (context.getRuntime().getDebug().isTrue()) e.printStackTrace();
                throw context.getRuntime().newTypeError("Could not dispatch to " + className + "#" + methodName + " using " + prettySig);
            }
        }
