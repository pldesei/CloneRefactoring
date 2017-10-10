(startLine=1086 endLine=1101 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.edit.ui/src/org/eclipse/emf/edit/ui/provider/DiagnosticDecorator.java)
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
