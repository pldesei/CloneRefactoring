        try {
            callEach(runtime, context, self, block.arity(), new BlockCallback() {
                boolean memo = false;
                public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                    IRubyObject larg = checkArgs(runtime, largs);
                    if (!memo && !block.yield(ctx, larg).isTrue()) memo = true;
                    if (memo) synchronized (result) {
                        result.append(larg);
                    }
                    return runtime.getNil();
                }
            });
        } catch (JumpException.SpecialJump sj) {}
