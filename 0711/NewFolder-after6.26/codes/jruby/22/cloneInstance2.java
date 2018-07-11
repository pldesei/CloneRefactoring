    public static IRubyObject min(ThreadContext context, IRubyObject self, final Block block) {
        final Ruby runtime = self.getRuntime();
        final IRubyObject result[] = new IRubyObject[] { null };
        final ThreadContext localContext = context;

        if (block.isGiven()) {
            callEach(runtime, context, self, new BlockCallback() {
                public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                    if (localContext != ctx) {
                        throw runtime.newThreadError("Enumerable#min{} cannot be parallelized");
                    }
                    if (result[0] == null || RubyComparable.cmpint(ctx, block.yield(ctx, 
                            runtime.newArray(largs[0], result[0])), largs[0], result[0]) < 0) {
                        result[0] = largs[0];
                    }
                    return runtime.getNil();
                }
            });
        } else {
            callEach(runtime, context, self, new BlockCallback() {
                public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                    synchronized (result) {
                        if (result[0] == null || RubyComparable.cmpint(ctx, largs[0].callMethod(ctx,
                                MethodIndex.OP_SPACESHIP, "<=>", result[0]), largs[0], result[0]) < 0) {
                            result[0] = largs[0];
                        }
                    }
                    return runtime.getNil();
                }
            });
        }
        
        return result[0] == null ? runtime.getNil() : result[0];
    }
