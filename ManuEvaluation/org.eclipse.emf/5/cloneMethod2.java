This clone method is located in File: plugins/org.eclipse.emf.ecore.xcore/src/org/eclipse/emf/ecore/xcore/util/XcoreEcoreBuilder.java
The line range of this clone method is: 824-851
The content of this clone method is as follows:
    public void initialize()
    {
      if (needsInitialization)
      {
        needsInitialization = false;

        JvmTypeReference instanceType = xClassifier.getInstanceType();
        if (instanceType != null)
        {
          String instanceTypeName = instanceType.getIdentifier();
          String normalizedInstanceTypeName = EcoreUtil.toJavaInstanceTypeName(
            (EGenericType)EcoreValidator.EGenericTypeBuilder.INSTANCE.parseInstanceTypeName(instanceTypeName).getData().get(0));
          setInstanceTypeName(normalizedInstanceTypeName);
          if (classLoader != null)
          {
            try
            {
              Class<?> instanceClass = classLoader.loadClass(getInstanceClassName());
              setInstanceClassGen(instanceClass);
            }
            catch (Throwable throwable)
            {
              // Ignore.
            }
          }
        }
      }
    }
