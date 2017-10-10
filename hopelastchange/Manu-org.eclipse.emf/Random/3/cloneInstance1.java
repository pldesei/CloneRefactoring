(startLine=1002 endLine=1021 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.codegen.ecore/src/org/eclipse/emf/codegen/ecore/genmodel/generator/GenPackageGeneratorAdapter.java)
  protected void generateModelIcon(GenPackage genPackage, Monitor monitor)
  {
    if (genPackage.hasConcreteClasses())
    {
      message = CodeGenEcorePlugin.INSTANCE.getString
        ("_UI_GeneratingModelIcon_message", new Object[] { genPackage.getModelIconFileName() });
      monitor.subTask(message);
      generateGIF
        (genPackage.getModelIconFileName(),
         getGIFEmitter(getInputPathNames(), MODEL_ICON_ID),
         genPackage.getPrefix(),
         null,
         false,
         createMonitor(monitor, 1));
    }
    else
    {
      monitor.worked(1);
    }
  }
