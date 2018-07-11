            } else if(val instanceof RubyArray) {
                obj = val;
                if(val == this) {
                    throw getRuntime().newArgumentError("recursive array join");
                } else {
                    final RubyArray ary = (RubyArray)val;
                    final IRubyObject outobj = obj;
                    getRuntime().execRecursive(new Ruby.RecursiveFunction() {
                            public IRubyObject call(IRubyObject obj, boolean recur) {
                                if(recur) {
                                    throw getRuntime().newArgumentError("recursive array join");
                                } else {
                                    ((RubyArray)ary).join1(context, outobj, sep, 0, result);
                                }
                                return getRuntime().getNil();
                            }
                        }, obj);
                }
            } else {
