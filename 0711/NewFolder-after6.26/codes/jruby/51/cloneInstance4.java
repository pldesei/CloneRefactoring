        try {
            callEach(runtime, context, self, Arity.NO_ARGUMENTS, new BlockCallback() {
                long i = len; // Atomic ?
                public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                    IRubyObject larg = checkArgs(runtime, largs);
                    synchronized (result) {
                        if (i == 0) {
                            result.append(larg);
                        } else {
                            --i;
                        }
                    }
                    return runtime.getNil();
                }
            });
        } catch (JumpException.SpecialJump sj) {}
