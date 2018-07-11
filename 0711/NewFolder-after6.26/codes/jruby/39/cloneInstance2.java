        for (int i = 0; i < superTypes.length; i++) {
            superTypeNames[i] = p(superTypes[i]);
            
            for (Method method : superTypes[i].getMethods()) {
                List<Method> methods = simpleToAll.get(method.getName());
                if (methods == null) simpleToAll.put(method.getName(), methods = new ArrayList<Method>());
                methods.add(method);
            }
        }
