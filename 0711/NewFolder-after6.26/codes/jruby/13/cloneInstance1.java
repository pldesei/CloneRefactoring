        public IRubyObject call(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
            RubyClass selfType = self.getMetaClass();

            if (cachedType == selfType && cachedMethod != null) {
                return cachedMethod.call(context, self, selfType, methodName, args, block);
            }

            DynamicMethod method = selfType.searchMethod(methodName);

            if (method.isUndefined() || (!methodName.equals("method_missing") && !method.isCallableFrom(context.getFrameSelf(), callType))) {
                return RuntimeHelpers.callMethodMissing(context, self, method, methodName, args, context.getFrameSelf(), callType, block);
            }

            cachedMethod = method;
            cachedType = selfType;

            selfType.getRuntime().getCacheMap().add(method, this);

            return method.call(context, self, selfType, methodName, args, block);
        }
