    public static Class createOldStyleImplClass(Class[] superTypes, RubyClass rubyClass, Ruby ruby, String name) {
        String[] superTypeNames = new String[superTypes.length];
        Map<String, List<Method>> simpleToAll = new HashMap<String, List<Method>>();
        for (int i = 0; i < superTypes.length; i++) {
            superTypeNames[i] = p(superTypes[i]);
            
            for (Method method : superTypes[i].getMethods()) {
                List<Method> methods = simpleToAll.get(method.getName());
                if (methods == null) simpleToAll.put(method.getName(), methods = new ArrayList<Method>());
                methods.add(method);
            }
        }
        
        Class newClass = defineOldStyleImplClass(ruby, name, superTypeNames, simpleToAll);
        
        return newClass;
    }
