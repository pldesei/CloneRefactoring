(startLine=517 endLine=541 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.ecore.xcore/src/org/eclipse/emf/ecore/xcore/util/XcoreJvmInferrer.java)
                  protected JvmOperation inferStructure()
                  {
                    JvmOperation validateClassifierOperation = createJvmOperation(genPackage, JvmVisibility.PUBLIC, false, getJvmTypeReference("boolean", genPackage));
                    EList<JvmFormalParameter> parameters = validateClassifierOperation.getParameters();
                    JvmElementInferrer<JvmFormalParameter> classifierParameterInferrer =
                        new JvmElementInferrer<JvmFormalParameter>(X_LOW)
                        {
                           @Override
                           protected JvmFormalParameter inferStructure()
                           {
                             return createJvmFormalParameter(genPackage, getJvmTypeReference(genClassifier.getImportedWildcardInstanceClassName(), genPackage));
                           }

                           @Override
                           public void inferName()
                           {
                             inferredElement.setName(genClassifier.getSafeUncapName());
                           }
                        };
                    associate(genClassifier, classifierParameterInferrer);
                    parameters.add(classifierParameterInferrer.getInferredElement());
                    parameters.add(createJvmFormalParameter(genPackage, diagnostics, getJvmTypeReference("org.eclipse.emf.common.util.DiagnosticChain", genPackage)));
                    parameters.add(createJvmFormalParameter(genPackage, context, getJvmTypeReference(map, genPackage)));
                    return validateClassifierOperation;
                  }
