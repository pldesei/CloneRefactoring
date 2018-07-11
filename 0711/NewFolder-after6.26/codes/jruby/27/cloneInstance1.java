        try {
            RubyClass selfType = pollAndGetClass(context, self);
            CacheEntry myCache = cache;
            if (myCache.typeOk(selfType)) {
                return myCache.method.call(context, self, selfType, methodName, args, block);
            }
            return cacheAndCall(caller, selfType, block, args, context, self);
        } catch (JumpException.BreakJump bj) {
