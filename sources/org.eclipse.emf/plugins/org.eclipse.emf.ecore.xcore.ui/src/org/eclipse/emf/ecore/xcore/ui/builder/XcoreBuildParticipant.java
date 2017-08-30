/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.ecore.xcore.ui.builder;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xcore.ui.internal.XcoreActivator;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.builder.BuilderParticipant;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.osgi.framework.Bundle;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class XcoreBuildParticipant extends BuilderParticipant
{
   protected Set<IProject> newProjects = Sets.newLinkedHashSet();

   @Override
  public void build(final IBuildContext context, IProgressMonitor monitor) throws CoreException
  {
    super.build(context, monitor);
    final Set<IProject> createdProjects = Sets.newHashSet();
    for (IProject newProject : newProjects)
    {
      if (newProject.exists())
      {
        createdProjects.add(newProject);
      }
    }
    if (!createdProjects.isEmpty())
    {
      IWorkbench workbench = PlatformUI.getWorkbench();
      final Set<IWorkingSet> workingSets = Sets.newHashSet();
      IProject project = context.getBuiltProject();
      for (IWorkingSet workingSet : workbench.getWorkingSetManager().getAllWorkingSets())
      {
        IAdaptable[] elements = workingSet.getElements();
        for (IAdaptable element : elements)
        {
          if (project.equals(element.getAdapter(IProject.class)))
          {
            workingSets.add(workingSet);
            continue;
          }
        }
      }

      final Bundle bundle = XcoreActivator.getInstance().getBundle();
      Job job =
        new Job(Platform.getResourceBundle(bundle).getString("_UI_NewProjectBuild_job"))
        {
          @Override
          protected IStatus run(IProgressMonitor monitor)
          {
            for (IWorkingSet workingSet : workingSets)
            {
              List<IAdaptable> elements = Lists.newArrayList(workingSet.getElements());
              elements.addAll(createdProjects);
              workingSet.setElements(workingSet.adaptElements(elements.toArray(new IAdaptable[elements.size()])));
            }

            for (IProject project : createdProjects)
            {
              try
              {
                project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
              }
              catch (CoreException exception)
              {
                return new Status(exception.getStatus().getSeverity(), bundle.getSymbolicName(), exception.getMessage(), exception);
              }
            }

            return Status.OK_STATUS;
          }
        };
      job.setPriority(Job.BUILD);
      job.schedule();
    }
    newProjects.clear();
  }

   @Override
  protected void handleChangedContents(Delta delta, IBuildContext context, EclipseResourceFileSystemAccess2 fileSystemAccess) throws CoreException
  {
    // Determine if this resource is logically nested in the project being built.
    //
    URI uri = delta.getUri();
    IProject builtProject = context.getBuiltProject();
    if (uri.isPlatformResource() && builtProject.getName().equals(uri.segment(1)))
    {
      // Determine which projects existed before we run the generator.
      //
      Resource resource = context.getResourceSet().getResource(uri, true);
      GenModel genModel = (GenModel)EcoreUtil.getObjectByType(resource.getContents(), GenModelPackage.Literals.GEN_MODEL);
      if (genModel != null)
      {
        IWorkspaceRoot root = builtProject.getWorkspace().getRoot();
        for (String projectName : new String [] { genModel.getEditProjectDirectory(), genModel.getEditorProjectDirectory(), genModel.getTestsProjectDirectory() })
        {
          if (!Strings.isNullOrEmpty(projectName))
          {
            IProject project = root.getProject(projectName);
            if (!project.exists())
            {
              newProjects.add(project);
            }
          }
        }
      }

      // Do the normal generation.
      //
      super.handleChangedContents(delta, context, fileSystemAccess);
    }
  }

  @Override
  protected boolean shouldGenerate(Resource resource, IBuildContext context)
  {
    return true;
  }
}
