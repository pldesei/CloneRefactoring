    public static RubyClass createImplClass(Class[] superTypes, Ruby ruby, String name) {
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
        
        Class newClass = defineImplClass(ruby, name, superTypeNames, simpleToAll);
        RubyClass rubyCls = populateImplClass(ruby, newClass, simpleToAll);
        
        return rubyCls;
    }
