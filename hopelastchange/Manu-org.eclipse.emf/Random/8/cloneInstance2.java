(startLine=1023 endLine=1042 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.codegen.ecore/src/org/eclipse/emf/codegen/ecore/genmodel/generator/GenPackageGeneratorAdapter.java)
  protected void generateModelWizardIcon(GenPackage genPackage, Monitor monitor)
  {
    if (genPackage.hasConcreteClasses() && genPackage.isGenerateModelWizard())
    {
      message = CodeGenEcorePlugin.INSTANCE.getString
        ("_UI_GeneratingModelWizardIcon_message", new Object[] { genPackage.getModelWizardIconFileName() });
      monitor.subTask(message);
      generateGIF
        (genPackage.getModelWizardIconFileName(),
         getGIFEmitter(getInputPathNames(), MODEL_WIZARD_ICON_ID),
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
