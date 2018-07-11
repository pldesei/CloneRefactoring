            } else {
                callEach(runtime, context, self, Arity.ONE_REQUIRED, new BlockCallback() {
                    public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                        checkContext(localContext, ctx, "any?");
                        IRubyObject larg = checkArgs(runtime, largs);
                        if (larg.isTrue()) {
                            throw JumpException.SPECIAL_JUMP;
                        }
                        return runtime.getNil();
                    }
                });
            }
