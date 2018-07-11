        if (javaCallable != null) {
            // no constructor support yet
            if (javaCallable instanceof org.jruby.javasupport.JavaMethod) {
                org.jruby.javasupport.JavaMethod javaMethod = (org.jruby.javasupport.JavaMethod)javaCallable;
                Method method = (Method)javaMethod.getValue();
                // only public, since non-public don't bind
                if (Modifier.isPublic(method.getModifiers()) && Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                    setNativeCall(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes(), Modifier.isStatic(method.getModifiers()), true);
                }
            }
        } else {
