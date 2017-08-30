/**
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;

/**
 * Provides methods to simplify the work with {@link IMarker}s.  The main goal is to
 * simplify the creation of markers using the information described in 
 * {@link Diagnostic}s. 
 * 
 * @since 2.2.0
 */
public class MarkerHelper
{
  /**
   * Returns the {@link Diagnostic#getSource() source} value used for {@link #getMarkerDiagnostics(Object, IFile) creating diagnostics} from markers.
   * @since 2.9
   */
  public String getDiagnosticSource()
  {
    return "org.eclipse.emf.common.ui.markers";
  }

  protected String getMarkerID()
  {
    return "org.eclipse.core.resources.problemmarker";
  }  
  
  protected IFile getFile(Object datum)
  {
    if (datum instanceof IFileEditorInput)
    {
      return ((IFileEditorInput)datum).getFile();
    }
    else if (datum instanceof IFile)
    {
      return (IFile)datum;
    }
    else if (datum instanceof Diagnostic)
    {
      return getFile((Diagnostic)datum);
    }
    return null;
  }

  protected IFile getFile(Diagnostic diagnostic)
  {
    List<?> data = diagnostic.getData();
    if (data != null)
    {
      for (Object datum : data)
      {
        IFile result = getFile(datum);
        if (result != null)
        {
          return result;
        }
      }
    }
    return null;
  }

  protected IFile getFile(URI uri)
  {
    return uri.isPlatformResource() && uri.segmentCount() > 2 ? 
      ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri.toPlatformString(true))) :
      null;
  }

  /**
   * <p>Creates a marker based on the information available in the specified
   * diagnostic.  The marker's id is defined by {@link #getMarkerID()}.</p>
   * <p>The default implementation looks in the diagnostic's data array for
   * objects that would allow an IFile to be computed</p> 
   * @param diagnostic
   * @throws CoreException
   */
  public void createMarkers(final Diagnostic diagnostic) throws CoreException
  {
    ResourcesPlugin.getWorkspace().run
      (new IWorkspaceRunnable()
       {
         public void run(IProgressMonitor monitor) throws CoreException
         {
           if (diagnostic.getChildren().isEmpty())
           {
             createMarkers(getFile(diagnostic), diagnostic, null);
           }
           else if (diagnostic.getMessage() == null)
           {
             for (Diagnostic childDiagnostic : diagnostic.getChildren())
             {
               createMarkers(childDiagnostic);
             }
           }
           else
           {
             for (Diagnostic childDiagnostic : diagnostic.getChildren())
             {
               createMarkers(getFile(childDiagnostic), childDiagnostic, diagnostic);
             }
           }
         }
       },
       null);
  }

  protected void createMarkers(IResource resource, Diagnostic diagnostic, Diagnostic parentDiagnostic) throws CoreException
  {
    if (resource != null && resource.exists())
    {
      IMarker marker = resource.createMarker(getMarkerID());
      int severity = diagnostic.getSeverity();
      if (severity < Diagnostic.WARNING)
      {
        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
      }
      else if (severity < Diagnostic.ERROR)
      {
        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
      }
      else
      {
        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
      }
          
      String message = composeMessage(diagnostic, parentDiagnostic);
      if (message != null)
      {
        marker.setAttribute(IMarker.MESSAGE, message);
      }
          
      adjustMarker(marker, diagnostic, parentDiagnostic);
    }
  }
  
  /**
   * Clients should override this method to update the marker associated with the diagnostic.
   * @param marker the marker to be updated.
   * @param diagnostic the diagnostic associated with the marker.
   * @param parentDiagnostic the parent of the diagnostic, if any.
   * @throws CoreException
   */
  protected void adjustMarker(IMarker marker, Diagnostic diagnostic, Diagnostic parentDiagnostic) throws CoreException
  {
    // Subclasses may override
  }  
      
  /**
   * Returns the message that will be used with the marker associated with the given diagnostic.
   * @param diagnostic the diagnostic.
   * @param parentDiagnostic the parent of the diagnostic, if any. 
   * @return the message that will be used with the marker associated with the given diagnostic.
   */
  protected String composeMessage(Diagnostic diagnostic, Diagnostic parentDiagnostic)
  {
    return diagnostic.getMessage();
  }

  /**
   * Returns whether the a maker with id equals to the return of {@link #getMarkerID()}
   * is available in the IResource computed from the specified object.
   * @param object
   * @return boolean
   */
  public boolean hasMarkers(Object object)
  {
    return hasMarkers(object, false, IResource.DEPTH_ZERO);
  }
  
  public boolean hasMarkers(Object object, boolean includeSubtypes, int depth)
  {
    return hasMarkers(getFile(object), includeSubtypes, depth);
  }

  protected boolean hasMarkers(IResource resource, boolean includeSubtypes, int depth)
  {
    if (resource != null && resource.exists())
    {
      try
      {
        IMarker[] markers = resource.findMarkers(getMarkerID(), includeSubtypes, depth);
        return markers.length > 0;
      }
      catch (CoreException e)
      {
        // Ignore
      }
    }
    return false;
  }

  /**
   * Deletes a maker with id equals to the return of {@link #getMarkerID()}
   * from the IResource computed from the specified object.
   * @param object
   */  
  public void deleteMarkers(Object object)
  {
    deleteMarkers(object, false, IResource.DEPTH_ZERO);
  }

  public void deleteMarkers(Object object, boolean includeSubtypes, int depth)
  {
    deleteMarkers(getFile(object), includeSubtypes, depth);
  }
  
  protected void deleteMarkers(final IResource resource, final boolean includeSubtypes, final int depth)
  {
    if (resource != null && resource.exists())
    {
      try
      {
        resource.getWorkspace().run
          (new IWorkspaceRunnable()
           {
             public void run(IProgressMonitor monitor) throws CoreException
             {
               resource.deleteMarkers(getMarkerID(), includeSubtypes, depth);
             }
           },
           null);
      }
      catch (CoreException e)
      {
        CommonUIPlugin.INSTANCE.log(e);
      }
    }
  }  
  
  public IEditorInput getEditorInput(Object object)
  {
    IFile file = getFile(object);
    if (file != null)
    {
      return new FileEditorInput(file);
    }
    else
    {
      return null;
    }
  }

  /**
   * @since 2.3
   */
  public List<?> getTargetObjects(Object object, IMarker marker)
  {
    return getTargetObjects(object, marker, true);
  }

  /**
   * @since 2.11
   */
  public List<?> getTargetObjects(Object object, IMarker marker, boolean wrap)
  {
    return Collections.EMPTY_LIST;
  }

  /**
   * Converts markers in the file to diagnostics.
   * @since 2.9
   */
  public Diagnostic getMarkerDiagnostics(Object object, IFile file)
  {
    return getMarkerDiagnostics(object, file, true);
  }

  /**
   * @since 2.11
   */
  public Diagnostic getMarkerDiagnostics(Object object, IFile file, boolean wrap)
  {
    BasicDiagnostic diagnostic = new BasicDiagnostic(null, 0, getDiagnosticSource(), new Object[] { object });
    try
    {
      for (IMarker marker : file.findMarkers(null, true, IResource.DEPTH_ZERO))
      {
        String message = marker.getAttribute(IMarker.MESSAGE, "");
        int severity = marker.getAttribute(IMarker.SEVERITY, Diagnostic.INFO);
        String sourceID = marker.getAttribute(IMarker.SOURCE_ID, "");
        diagnostic.add
        (new BasicDiagnostic
           (severity == IMarker.SEVERITY_ERROR ?
              Diagnostic.ERROR : 
              severity == IMarker.SEVERITY_WARNING ? 
              Diagnostic.WARNING : 
              Diagnostic.INFO, 
            sourceID,
            0, 
            message, 
            null));
      }

      for (Diagnostic instrincDiagnostic : getInstrinciDiagnostics(object, wrap))
      {
        diagnostic.add(instrincDiagnostic);
      }
    }
    catch (CoreException exception)
    {
      CommonUIPlugin.INSTANCE.log(exception);
    }
    return diagnostic;
  }

  /**
   * @since 2.13
   */
  public List<? extends Diagnostic> getInstrinciDiagnostics(Object object, boolean wrap)
  {
    return Collections.emptyList();
  }

  /**
   * {@link #deleteMarkers(Object) deletes} any markers associated with the resource of the given diagnostic and the {@link #createMarkers(Diagnostic) creates} new markers for it.
   * All  processing is done with as a single {@link IWorkspace#run(IWorkspaceRunnable, IProgressMonitor) workspace operation}.
   * @param diagnostic the diagnostic to process.
   * @throws CoreException if creating markers results in a core exception.
   * @since 2.13
   */
  public void updateMarkers(final Diagnostic diagnostic) throws CoreException
  {
    ResourcesPlugin.getWorkspace().run
      (new IWorkspaceRunnable()
       {
         public void run(IProgressMonitor monitor) throws CoreException
         {
           deleteMarkers(diagnostic);
           if (diagnostic.getSeverity() != Diagnostic.OK)
           {
             createMarkers(diagnostic);
           }
         }
       },
       null);
  }
}