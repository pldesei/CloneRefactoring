        try {
            callEach(runtime, context, self, Arity.NO_ARGUMENTS, new BlockCallback() {
                long i = len; // Atomic ?
                public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                    IRubyObject larg = checkArgs(runtime, largs);
                    synchronized (result) {
                        result.append(larg);
                        if (--i == 0) throw JumpException.SPECIAL_JUMP; 
                    }
                    return runtime.getNil();
                }
            });
        } catch (JumpException.SpecialJump sj) {}
