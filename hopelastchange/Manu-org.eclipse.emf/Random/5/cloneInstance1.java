(startLine=176 endLine=186 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.ecore/src/org/eclipse/emf/ecore/resource/impl/PlatformContentHandlerImpl.java)
  protected String getCharset(URI uri, InputStream inputStream, Map<?, ?> options, Map<Object, Object> context) throws IOException
  {
    if (uri.isPlatformResource() && PlatformResourceURIHandlerImpl.workspaceRoot != null)
    {
      return PlatformResourceURIHandlerImpl.WorkbenchHelper.getCharset(uri.toPlatformString(true), options);
    }
    else
    {
      return super.getCharset(uri, inputStream, options, context);
    }
  }
