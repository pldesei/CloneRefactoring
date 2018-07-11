    private static String getShortNameFor(Ruby runtime, String nameOrOid) {
        ASN1ObjectIdentifier oid = getObjectIdentifier(runtime,nameOrOid);
        Map<String, ASN1ObjectIdentifier> em = getOIDLookup(runtime);
        String name = null;
        for(Iterator<String> iter = em.keySet().iterator();iter.hasNext();) {
            String key = iter.next();
            if(oid.equals(em.get(key))) {
                if(name == null || key.length() < name.length()) {
                    name = key;
                }
            }
        }
        return name;
    }
