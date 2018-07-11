        public static IRubyObject add_class_annotation(ThreadContext context, IRubyObject maybeClass, IRubyObject annoMap) {
            RubyClass clazz;
            if (maybeClass instanceof RubyClass) {
                clazz = (RubyClass)maybeClass;
            } else {
                throw context.getRuntime().newTypeError(maybeClass, context.getRuntime().getClassClass());
            }

            Map<Class,Map<String,Object>> annos = (Map<Class,Map<String,Object>>)annoMap;

            for (Map.Entry<Class,Map<String,Object>> entry : annos.entrySet()) {
                Map<String,Object> value = entry.getValue();
                if (value == null) value = Collections.EMPTY_MAP;
                clazz.addClassAnnotation(entry.getKey(), value);
            }

            return context.getRuntime().getNil();
        }
