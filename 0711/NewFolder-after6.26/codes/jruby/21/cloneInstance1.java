        public IRubyObject call(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
            context.callThreadPoll();

            try {
                RubyClass selfType = self.getMetaClass();

                CacheEntry myCache = cache;
                if (myCache.cachedType == selfType) {
                    return myCache.cachedMethod.call(context, self, selfType, methodName, args, block);
                }

                return cacheAndCall(selfType, block, args, context, self);
            } catch (JumpException.BreakJump bj) {
                return handleBreakJump(bj, block);
            } catch (JumpException.RetryJump rj) {
                throw context.getRuntime().newLocalJumpError("retry", context.getRuntime().getNil(), "retry outside of rescue not yet supported");
            } catch (StackOverflowError soe) {
                throw context.getRuntime().newSystemStackError("stack level too deep");
            }
        }
