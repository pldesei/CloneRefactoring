        } else {
            // use the lowest-arity non-overload
            for (JavaCallable[] callablesForArity : javaCallables) {
                if (callablesForArity != null
                        && callablesForArity.length == 1
                        && callablesForArity[0] instanceof org.jruby.javasupport.JavaMethod) {
                    Method method = (Method)((org.jruby.javasupport.JavaMethod)callablesForArity[0]).getValue();
                    // only public, since non-public don't bind
                    if (Modifier.isPublic(method.getModifiers()) && Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                        setNativeCall(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes(), Modifier.isStatic(method.getModifiers()), true);
                    }
                }
            }
        }
