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

                        switch ( simpleName ) {
                            // TODO: this code should really check if a Ruby equals method is implemented or not.
                            case "equals" :
                                if ( defineDefaultEquals(2, mv, paramTypes, returnType) ) ;
                                else defineOldStyleBody(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                                break;
                            case "hashCode" :
                                if ( defineDefaultHashCode(3, mv, paramTypes, returnType) ) ;
                                else defineOldStyleBody(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                                break;
                            case "toString" :
                                if ( defineDefaultToString(4, mv, paramTypes, returnType) ) ;
                                else defineOldStyleBody(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                                break;
                            case "__ruby_object" :
                                if ( paramTypes.length == 0 && returnType == IRubyObject.class ) {
                                    mv.aload(0);
                                    mv.getfield(pathName, "$self", ci(IRubyObject.class));
                                    mv.areturn();
                                    break;
                                }
                            default : // cacheIndex = cacheSize++;
                                defineOldStyleBody(mv, pathName, simpleName, paramTypes, returnType, baseIndex, cacheSize++, nameSet);
                                break;
                        }

                        mv.end();
                    }
                }
