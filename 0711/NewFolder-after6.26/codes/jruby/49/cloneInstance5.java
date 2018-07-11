        try {
            callEach(runtime, context, self, Arity.ONE_ARGUMENT, new BlockCallback() {
                    public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                        IRubyObject larg = checkArgs(runtime, largs);
                        checkContext(localContext, ctx, "first");
                        holder[0] = larg;
                        throw JumpException.SPECIAL_JUMP;
                    }
                });
        } catch (JumpException.SpecialJump sj) {}
