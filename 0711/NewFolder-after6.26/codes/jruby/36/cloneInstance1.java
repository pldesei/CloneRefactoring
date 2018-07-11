        public static IRubyObject add_method_annotation(ThreadContext context, IRubyObject maybeClass, IRubyObject methodName, IRubyObject annoMap) {
            RubyClass clazz;
            if (maybeClass instanceof RubyClass) {
                clazz = (RubyClass)maybeClass;
            } else {
                throw context.getRuntime().newTypeError(maybeClass, context.getRuntime().getClassClass());
            }

            String method = methodName.convertToString().asJavaString();

            Map<Class,Map<String,Object>> annos = (Map<Class,Map<String,Object>>)annoMap;

            for (Map.Entry<Class,Map<String,Object>> entry : annos.entrySet()) {
                Map<String,Object> value = entry.getValue();
                if (value == null) value = Collections.EMPTY_MAP;
                clazz.addMethodAnnotation(method, entry.getKey(), value);
            }

            return context.getRuntime().getNil();
        }
