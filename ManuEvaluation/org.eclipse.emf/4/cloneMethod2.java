This clone method is located in File: plugins/org.eclipse.emf.edit.ui/src/org/eclipse/emf/edit/ui/provider/DiagnosticDecorator.java
The line range of this clone method is: 1086-1101
The content of this clone method is as follows:
    protected void refreshResourceDiagnostics(List<Resource> resources)
    {
      final BasicDiagnostic diagnostic =
        new BasicDiagnostic
          (EObjectValidator.DIAGNOSTIC_SOURCE,
           0,
           EMFEditUIPlugin.INSTANCE.getString("_UI_DiagnosisOfNObjects_message", new String[] { "" + resources.size() }),
           new Object [] { resourceSet } );
      for (Resource resource : resources)
      {
        diagnostic.add(markerHelper.getMarkerDiagnostics(resource, null, false));
        liveValidator.scheduleValidation(resource);
      }
      diagnostics.clear();
      updateDiagnostic(diagnostic);
    }
