            public void processMethodDeclarationMulti(MethodDeclaration md) {
                JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                if (anno != null && out != null) {
                    boolean isStatic = md.getModifiers().contains(Modifier.STATIC);
                    
                    // declared type returns the qualified name without $ for inner classes!!!
                    String qualifiedName;
                    if (md.getDeclaringType().getDeclaringType() != null) {
                        // inner class, use $ to delimit
                        if (md.getDeclaringType().getDeclaringType().getDeclaringType() != null) {
                            qualifiedName = md.getDeclaringType().getDeclaringType().getDeclaringType().getQualifiedName() + "$" + md.getDeclaringType().getDeclaringType().getSimpleName() + "$" + md.getDeclaringType().getSimpleName();
                        } else {
                            qualifiedName = md.getDeclaringType().getDeclaringType().getQualifiedName() + "$" + md.getDeclaringType().getSimpleName();
                        }
                    } else {
                        qualifiedName = md.getDeclaringType().getQualifiedName();
                    }
                    
                    boolean hasContext = false;
                    boolean hasBlock = false;
                    
                    for (ParameterDeclaration pd : md.getParameters()) {
                        hasContext |= pd.getType().toString().equals("org.jruby.runtime.ThreadContext");
                        hasBlock |= pd.getType().toString().equals("org.jruby.runtime.Block");
                    }
                    
                    int actualRequired = calculateActualRequired(md.getParameters().size(), anno.optional(), anno.rest(), isStatic, hasContext, hasBlock);
                    
                    String annotatedBindingName = CodegenUtils.getAnnotatedBindingClassName(
                            md.getSimpleName(),
                            qualifiedName,
                            isStatic,
                            actualRequired,
                            anno.optional(),
                            true);
                    
                    out.println("        javaMethod = new " + annotatedBindingName + "(cls, Visibility." + anno.visibility() + ");");
                    out.println("        javaMethod.setArity(Arity.OPTIONAL);");
                    out.println("        javaMethod.setJavaName(\"" + md.getSimpleName() + "\");");
                    out.println("        javaMethod.setSingleton(" + isStatic + ");");
                    out.println("        javaMethod.setCallConfig(CallConfiguration." + getCallConfigNameByAnno(anno) + ");");
                    generateMethodAddCalls(md, anno);
                }
            }
