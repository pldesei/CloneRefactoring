(startLine=1105 endLine=1112 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.ecore.xcore/src/org/eclipse/emf/ecore/xcore/util/XcoreJvmInferrer.java)
                        protected JvmOperation inferStructure()
                        {
                          JvmOperation jvmOperation = createJvmOperation(genPackage,JvmVisibility.PUBLIC, false, getJvmTypeReference(genDataType.getImportedParameterizedObjectInstanceClassName(), genDataType));
                          EList<JvmFormalParameter> parameters = jvmOperation.getParameters();
                          parameters.add(createJvmFormalParameter(genPackage, "eDataType", getJvmTypeReference("org.eclipse.emf.ecore.EDataType", genPackage)));
                          parameters.add(createJvmFormalParameter(genPackage, "initialValue", getJvmTypeReference("java.lang.String", genPackage)));
                          return jvmOperation;
                        }
