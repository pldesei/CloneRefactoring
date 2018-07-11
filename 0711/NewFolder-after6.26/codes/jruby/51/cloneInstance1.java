        if (block.isGiven()) {
            result = new int[] { 0 };
            callEach(runtime, context, self, block.arity(), new BlockCallback() {
                public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                    checkContext(localContext, ctx, "count");
                    IRubyObject larg = checkArgs(runtime, largs);
                    if (block.yield(ctx, larg).isTrue()) {
                        result[0]++; 
                    }
                    return runtime.getNil();
                }
            });
        } else {
