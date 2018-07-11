        for (Map.Entry<String, List<Method>> entry : simpleToAll.entrySet()) {
            final String simpleName = entry.getKey();
            final List<Method> methods = entry.getValue();
            Set<String> nameSet = JavaUtil.getRubyNamesForJavaName(simpleName, methods);

            implementedNames.clear();

            for (int i = 0; i < methods.size(); i++) {
                final Method method = methods.get(i);
                final Class[] paramTypes = method.getParameterTypes();
                final Class returnType = method.getReturnType();

                String fullName = simpleName + prettyParams(paramTypes);
                if (implementedNames.contains(fullName)) continue;
                implementedNames.add(fullName);

                // indices for temp values
                int baseIndex = 1;
                for (Class paramType : paramTypes) {
                    if (paramType == double.class || paramType == long.class) {
                        baseIndex += 2;
                    } else {
                        baseIndex += 1;
                    }
                }

                SkinnyMethodAdapter mv = new SkinnyMethodAdapter(
                        cw, ACC_PUBLIC, simpleName, sig(returnType, paramTypes), null, null);
                mv.start();
                mv.line(1);

                switch ( simpleName ) { // cacheIndex = cacheSize++;

                    case "equals" :
                        if ( paramTypes.length == 1 && paramTypes[0] == Object.class && returnType == Boolean.TYPE ) {
                            defineRealEqualsWithFallback(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                        }
                        else defineRealBody(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                        break;
                    case "hashCode" :
                        if ( paramTypes.length == 0 && returnType == Integer.TYPE ) {
                            defineRealHashCodeWithFallback(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                        }
                        else defineRealBody(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                        break;
                    case "toString" :
                        if ( paramTypes.length == 0 && returnType == String.class ) {
                            defineRealToStringWithFallback(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                        }
                        else defineRealBody(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                        break;
                    default :
                        defineRealBody(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                        break;
                }

                mv.end();
            }
        }
