This clone fragment is located in File: plugins/org.eclipse.emf.ecore/src/org/eclipse/emf/ecore/resource/impl/PlatformContentHandlerImpl.java
The line range of this clone fragment is: 176-186
The content of this clone fragment is as follows:
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
