(startLine=824 endLine=851 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.ecore.xcore/src/org/eclipse/emf/ecore/xcore/util/XcoreEcoreBuilder.java)
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
