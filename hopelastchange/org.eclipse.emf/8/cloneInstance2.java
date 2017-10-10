This clone instance is located in File: plugins/org.eclipse.emf.codegen.ecore/src/org/eclipse/emf/codegen/ecore/genmodel/generator/GenPackageGeneratorAdapter.java
The line range of this clone instance is: 1023-1042
The content of this clone instance is as follows:
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
