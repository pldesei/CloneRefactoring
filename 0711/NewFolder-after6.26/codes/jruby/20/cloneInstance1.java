        public IRubyObject call(ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2) {
            context.callThreadPoll();

            RubyClass selfType = self.getMetaClass();

            CacheEntry myCache = cache;
            if (myCache.cachedType == selfType) {
                    return myCache.cachedMethod.call(context, self, selfType, methodName, arg1, arg2);
            }

            return cacheAndCall(selfType, context, self, arg1, arg2);
        }
