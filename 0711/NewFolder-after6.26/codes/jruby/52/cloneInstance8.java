            if (block.isGiven()) {
                callEach(runtime, context, self, block.arity(), new BlockCallback() {
                    public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                        checkContext(localContext, ctx, "any?");
                        IRubyObject larg = checkArgs(runtime, largs);
                        if (block.yield(ctx, larg).isTrue()) {
                            throw JumpException.SPECIAL_JUMP;
                        }
                        return runtime.getNil();
                    }
                });
            } else {
