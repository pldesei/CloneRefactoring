        if (Character.isLowerCase(name.charAt(0))) {
            // this covers primitives and (unlikely) lower-case class names
            try {
                return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
            } catch (RaiseException re) { /* not primitive or lc class */
                RubyException rubyEx = re.getException();
                if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                    RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                }
            } catch (Exception e) { /* not primitive or lc class */ }

            // TODO: check for Java reserved names and raise exception if encountered

            RubyModule packageModule;
            // TODO: decompose getJavaPackageModule so we don't parse fullName
            if ((packageModule = getJavaPackageModule(runtime, name)) == null) {
                return null;
            }
            RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
            if (javaModule.getMetaClass().isMethodBound(name, false)) {
                return packageModule;
            // save package module as ivar in parent, and add method to parent so
            // we don't have to come back here.
            }
            final String ivarName = ("@__pkg__" + name).intern();
            javaModule.fastSetInstanceVariable(ivarName, packageModule);
            RubyClass singleton = javaModule.getSingletonClass();
            singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, Visibility.PUBLIC) {

                public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                    if (args.length != 0) {
                        Arity.raiseArgumentError(runtime, args.length, 0, 0);
                    }
                    IRubyObject variable;
                    if ((variable = ((RubyModule) self).fastGetInstanceVariable(ivarName)) != null) {
                        return variable;
                    }
                    return runtime.getNil();
                }

                @Override
                public Arity getArity() {
                    return Arity.noArguments();
                }
            });
            return packageModule;
        } else {
