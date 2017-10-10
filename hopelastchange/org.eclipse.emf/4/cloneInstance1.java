This clone instance is located in File: plugins/org.eclipse.emf.edit.ui/src/org/eclipse/emf/edit/ui/provider/DiagnosticDecorator.java
The line range of this clone instance is: 1066-1084
The content of this clone instance is as follows:
    protected void handleResourceDiagnostics(List<Resource> resources)
    {
      final BasicDiagnostic diagnostic =
        new BasicDiagnostic
          (EObjectValidator.DIAGNOSTIC_SOURCE,
           0,
           EMFEditUIPlugin.INSTANCE.getString("_UI_DiagnosisOfNObjects_message", new String[] { "" + resources.size() }),
           new Object [] { resourceSet } );
      LiveValidator liveValidator = getLiveValidator();
      if (liveValidator != null)
      {
        for (Resource resource : resources)
        {
        diagnostic.add(markerHelper.getMarkerDiagnostics(resource, null, false));
        liveValidator.scheduleValidation(resource);
        }
      }
      updateDiagnostic(diagnostic);
    }
