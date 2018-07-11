    public RubyArray private_instance_methods(RubyObject[] args) {
        boolean includeSuper = false;

        if (args.length > 0) {
            includeSuper = args[0].isTrue();
        }

        return methodList(includeSuper, new RubyMapMethod() {
            public int execute(Object key, Object value, Object arg) {
                // cast args
                String id = (String) key;
                IMethod method = (IMethod) value;
                RubyArray ary = (RubyArray) arg;

                if (method == null) {
                    ary.append(getRuby().getNil());
                    ary.append(RubyString.newString(getRuby(), id));
                } else if ((method.getNoex() & Constants.NOEX_PRIVATE) != 0) {
                    RubyString name = RubyString.newString(getRuby(), id);

                    if (! ary.includes(name)) {
                        ary.append(name);
                    }
                } else if (method instanceof EvaluateMethod && ((EvaluateMethod) method).getNode() instanceof ZSuperNode) {
                    ary.append(getRuby().getNil());
                    ary.append(RubyString.newString(getRuby(), id));
                }
                return RubyMapMethod.CONTINUE;
            }
        });
    }
