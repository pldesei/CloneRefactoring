        public IRubyObject call(ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2, Block block) {
            context.callThreadPoll();

            try {
                RubyClass selfType = self.getMetaClass();

                CacheEntry myCache = cache;
                if (myCache.cachedType == selfType) {
                    return myCache.cachedMethod.call(context, self, selfType, methodName, arg1, arg2, block);
                }

                return cacheAndCall(selfType, block, context, self, arg1, arg2);
            } catch (JumpException.BreakJump bj) {
                return handleBreakJump(bj, block);
            } catch (JumpException.RetryJump rj) {
                throw context.getRuntime().newLocalJumpError("retry", context.getRuntime().getNil(), "retry outside of rescue not yet supported");
            } catch (StackOverflowError soe) {
                throw context.getRuntime().newSystemStackError("stack level too deep");
            }
        }
