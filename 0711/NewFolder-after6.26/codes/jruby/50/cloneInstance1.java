    public static IRubyObject take_while(ThreadContext context, IRubyObject self, final Block block) {
        if (!block.isGiven()) return enumeratorize(context.getRuntime(), self, "take_while");

        final Ruby runtime = context.getRuntime();
        final RubyArray result = runtime.newArray();

        try {
            callEach(runtime, context, self, block.arity(), new BlockCallback() {
                public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                    IRubyObject larg = checkArgs(runtime, largs);
                    if (!block.yield(ctx, larg).isTrue()) throw JumpException.SPECIAL_JUMP;
                    synchronized (result) {
                        result.append(larg);
                    }
                    return runtime.getNil();
                }
            });
        } catch (JumpException.SpecialJump sj) {}
        return result;
    }    
