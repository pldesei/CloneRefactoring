    public IRubyObject dup() {
        try {
            MapJavaProxy proxy = new MapJavaProxy(getRuntime(), metaClass);
            Map newMap = (Map)getObject().getClass().newInstance();
            newMap.putAll((Map)getObject());
            proxy.setObject(newMap);
            return proxy;
        } catch (InstantiationException ex) {
            throw getRuntime().newNotImplementedError("can't dup Map of type " + getObject().getClass().getName());
        } catch (IllegalAccessException ex) {
            throw getRuntime().newNotImplementedError("can't dup Map of type " + getObject().getClass().getName());
        }
    }
