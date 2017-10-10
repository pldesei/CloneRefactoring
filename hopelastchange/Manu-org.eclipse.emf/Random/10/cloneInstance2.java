(startLine=194 endLine=204 srcPath=/root/Projects/newestVersion/org.eclipse.emf/00001/org.eclipse.emf/plugins/org.eclipse.emf.ecore/src/org/eclipse/emf/ecore/resource/impl/PlatformContentHandlerImpl.java)
  protected String getLineDelimiter(URI uri, InputStream inputStream, Map<?, ?> options, Map<Object, Object> context) throws IOException
  {
    if (uri.isPlatformResource() && PlatformResourceURIHandlerImpl.workspaceRoot != null)
    {
      return PlatformResourceURIHandlerImpl.WorkbenchHelper.getLineDelimiter(uri.toPlatformString(true), options);
    }
    else
    {
      return super.getCharset(uri, inputStream, options, context);
    }
  }
