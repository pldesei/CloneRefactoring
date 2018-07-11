        if (!Character.isUpperCase(name.charAt(0))) {
            // filter out any Java primitive names
            // TODO: should check against all Java reserved names here, not just primitives
            if (JAVA_PRIMITIVES.containsKey(name)) {
                throw runtime.newArgumentError("illegal package name component: " + name);
            }
            
            // this covers the rare case of lower-case class names (and thus will
            // fail 99.999% of the time). fortunately, we'll only do this once per
            // package name. (and seriously, folks, look into best practices...)
            try {
                return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, fullName));
            } catch (RaiseException re) { /* expected */
                RubyException rubyEx = re.getException();
                if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                    RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                }
            } catch (Exception e) { /* expected */ }

            // Haven't found a class, continue on as though it were a package
            RubyModule packageModule;
            // TODO: decompose getJavaPackageModule so we don't parse fullName
            if ((packageModule = getJavaPackageModule(runtime, fullName)) == null) {
                return null;
            }
            
            // save package module as ivar in parent, and add method to parent so
            // we don't have to come back here.
            final String ivarName = ("@__pkg__" + name).intern();
            parentPackage.fastSetInstanceVariable(ivarName, packageModule);
            RubyClass singleton = parentPackage.getSingletonClass();
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
