(startLine=1066 endLine=1084 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.edit.ui/src/org/eclipse/emf/edit/ui/provider/DiagnosticDecorator.java)
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
