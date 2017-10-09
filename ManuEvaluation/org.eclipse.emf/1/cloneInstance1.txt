This clone instance is located in File: plugins/org.eclipse.emf.ecore.xcore/src/org/eclipse/emf/ecore/xcore/util/XcoreJvmInferrer.java
The line range of this clone instance is: 1105-1112
The content of this clone instance is as follows:
                        protected JvmOperation inferStructure()
                        {
                          JvmOperation jvmOperation = createJvmOperation(genPackage,JvmVisibility.PUBLIC, false, getJvmTypeReference(genDataType.getImportedParameterizedObjectInstanceClassName(), genDataType));
                          EList<JvmFormalParameter> parameters = jvmOperation.getParameters();
                          parameters.add(createJvmFormalParameter(genPackage, "eDataType", getJvmTypeReference("org.eclipse.emf.ecore.EDataType", genPackage)));
                          parameters.add(createJvmFormalParameter(genPackage, "initialValue", getJvmTypeReference("java.lang.String", genPackage)));
                          return jvmOperation;
                        }
