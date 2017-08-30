/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.ecore.xcore.ui.quickfix;


import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreValidator;
import org.eclipse.emf.ecore.xcore.XAttribute;
import org.eclipse.emf.ecore.xcore.XGenericType;
import org.eclipse.emf.ecore.xcore.formatting.XcoreImportOrganizer;
import org.eclipse.emf.ecore.xcore.validation.XcoreIssueCodes;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModification;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.util.TextRegion;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.ui.quickfix.XbaseQuickfixProvider;

import com.google.inject.Inject;
import com.google.inject.Provider;


public class XcoreQuickfixProvider extends XbaseQuickfixProvider
{
  @Inject
  protected Provider<XcoreImportOrganizer> xcoreImportOrganizerProvider;

  @Inject
  private IJavaProjectProvider projectProvider;

  @Inject
  private XcoreClasspathUpdater classpathUpdater;

  @Override
  protected void createLinkingIssueQuickfixes
    (Issue issue,
     IssueResolutionAcceptor
     issueResolutionAcceptor,
     IXtextDocument xtextDocument,
     XtextResource resource,
     EObject referenceOwner,
     EReference unresolvedReference) throws Exception
  {
    javaTypeQuickfixes.addQuickfixes(issue, issueResolutionAcceptor, xtextDocument, resource, referenceOwner, unresolvedReference);
  }

  @Fix(EcoreValidator.DIAGNOSTIC_SOURCE + '.' + EcoreValidator.CONSISTENT_TYPE_CLASS_NOT_PERMITTED)
  public void convertToReference(final Issue issue, final IssueResolutionAcceptor acceptor)
  {
    IModificationContext modificationContext = getModificationContextFactory().createModificationContext(issue);
    final IXtextDocument xtextDocument = modificationContext.getXtextDocument();
    xtextDocument.readOnly
      (new IUnitOfWork.Void<XtextResource>()
       {
         @Override
         public void process(XtextResource xtextResource) throws Exception
         {
           EObject cause = xtextResource.getResourceSet().getEObject(issue.getUriToProblem(), false);
           if (cause instanceof XGenericType)
           {
             XGenericType xGenericType = (XGenericType)cause;
             if (xGenericType.eContainer() instanceof XAttribute && xGenericType.getType() instanceof GenClass)
             {
               ICompositeNode node = NodeModelUtils.getNode(xGenericType.eContainer());
               String range = node == null ? xtextDocument.get(issue.getOffset(), issue.getLength()) : xtextDocument.get(node.getOffset(), node.getLength());
               quickFix(issue, "Convert to cross reference", "refers ", acceptor, range);
               quickFix(issue, "Convert to containment reference", "contains ", acceptor, range);
               quickFix(issue, "Convert to container reference", "container ", acceptor, range);
             }
           }
         }

         private void quickFix(final Issue issue, String description, final String replacement, final IssueResolutionAcceptor acceptor, String range)
         {
           acceptor.accept
              (issue,
               description,
               replacement + range,
               "full/obj16/correction_change.gif",
               new IModification()
               {
                 public void apply(IModificationContext context) throws BadLocationException
                 {
                   IXtextDocument xtextDocument = context.getXtextDocument();
                   xtextDocument.replace(issue.getOffset(), 0, replacement);
                 }
               });
         }
       });
  }

  public static class RemovalRegion
  {
    protected IXtextDocument xtextDocument;
    protected int begin;
    protected int end;
    protected int deleteBegin;
    protected int deleteEnd;
    protected String replacement;

    public RemovalRegion(IXtextDocument xtextDocument, EObject eObject) throws BadLocationException
    {
      this.xtextDocument = xtextDocument;

      // Determine the associated node.
      //
      ICompositeNode node = NodeModelUtils.getNode(eObject);
      if (node != null)
      {
        INode previous = node.getPreviousSibling();
        begin = getLineBeginOffsetOfOffset(previous == null ? node.getOffset() : previous.getOffset() + previous.getLength());
        INode next = node.getNextSibling();
        end = getLineEndOffsetOfOffset(next == null ? node.getOffset() + node.getLength() : next.getOffset());
  
        deleteBegin = getBeginOffset(node);
        deleteEnd = getEndOffset(node);
  
        replacement =
           deleteBegin > 0 && !Character.isWhitespace(xtextDocument.getChar(deleteBegin - 1)) && deleteEnd + 1 <  xtextDocument.getLength() && !Character.isWhitespace(xtextDocument.getChar(deleteEnd + 1)) ?
             " " :
             "";
      }
    }

    public int getBegin()
    {
      return begin;
    }

    public int getEnd()
    {
      return end;
    }

    public int getDeleteBegin()
    {
      return deleteBegin;
    }

    public int getDeleteEnd()
    {
      return deleteEnd;
    }

    public String getReplacement()
    {
      return replacement;
    }

    protected int getLineBeginOffsetOfOffset(int offset) throws BadLocationException
    {
      return xtextDocument.getLineOffset(xtextDocument.getLineOfOffset(offset));
    }

    protected int getLineEndOffsetOfOffset(int offset) throws BadLocationException
    {
      int lineOfOffset = xtextDocument.getLineOfOffset(offset);
      return
        lineOfOffset + 1 < xtextDocument.getNumberOfLines() ?
          xtextDocument.getLineOffset(lineOfOffset + 1) :
          xtextDocument.getLength();
    }

    protected int getBeginOffset(INode node) throws BadLocationException
    {
      int result = node.getOffset();
      INode previous = node.getPreviousSibling();
      if (previous != null)
      {
        int nodeLine = xtextDocument.getLineOfOffset(result);
        int previousLine = xtextDocument.getLineOfOffset(previous.getOffset() + previous.getLength());
        if (nodeLine == previousLine)
        {
          result = previous.getOffset() + previous.getLength();
        }
        else
        {
          while (--nodeLine > previousLine)
          {
            boolean isWhite = true;
            for (int j = xtextDocument.getLineOffset(nodeLine), end = j + xtextDocument.getLineLength(nodeLine); j < end; ++j)
            {
              if (!Character.isWhitespace(xtextDocument.getChar(j)))
              {
                isWhite = false;
                break;
              }
            }
            if (isWhite)
            {
              break;
            }
          }
          result = xtextDocument.getLineOffset(nodeLine + 1);
        }
      }
      else
      {
        result = getLineBeginOffsetOfOffset(result);
      }
      return result;
    }

    protected int getEndOffset(INode node) throws BadLocationException
    {
      int result = node.getOffset() + node.getLength();
      INode next = node.getNextSibling();
      if (next != null)
      {
        int nodeLine = xtextDocument.getLineOfOffset(result);
        int nextLine = xtextDocument.getLineOfOffset(next.getOffset() + next.getLength());
        if (nodeLine == nextLine)
        {
          result = next.getOffset();
        }
        else
        {
          result = getLineEndOffsetOfOffset(result);
        }
      }
      else
      {
        result = getLineEndOffsetOfOffset(result);
      }
      return result;
    }
  }

  @Fix(XcoreIssueCodes.COLLIDING_IMPORT)
  public void collidingImport(final Issue issue, final IssueResolutionAcceptor acceptor)
  {
    removeImport(issue, acceptor);
    oranizeImports(issue, acceptor);
  }

  @Fix(XcoreIssueCodes.DUPLICATE_IMPORT)
  public void duplicateImport(final Issue issue, final IssueResolutionAcceptor acceptor)
  {
    removeImport(issue, acceptor);
    oranizeImports(issue, acceptor);
  }

  @Fix(XcoreIssueCodes.UNUSED_IMPORT)
  public void unusedImport(final Issue issue, final IssueResolutionAcceptor acceptor)
  {
    removeImport(issue, acceptor);
    oranizeImports(issue, acceptor);
  }

  public void removeImport(final Issue issue, final IssueResolutionAcceptor acceptor)
  {
    IModificationContext modificationContext = getModificationContextFactory().createModificationContext(issue);
    final IXtextDocument xtextDocument = modificationContext.getXtextDocument();
    xtextDocument.readOnly
      (new IUnitOfWork.Void<XtextResource>()
       {
         @Override
         public void process(XtextResource xtextResource) throws Exception
         {
           // The cause is expected to be an import directive.
           //
           EObject cause = xtextResource.getResourceSet().getEObject(issue.getUriToProblem(), false);
           final RemovalRegion removalRegion = new RemovalRegion(xtextDocument, cause);
           acceptor.accept
             (issue,
              "Remove import",
              "...\n" +
                xtextDocument.get(removalRegion.begin, removalRegion.deleteBegin - removalRegion.begin) +
                removalRegion.replacement +
                xtextDocument.get(removalRegion.deleteEnd, removalRegion.end - removalRegion.deleteEnd) +
                "\n...",
              "full/obj16/delete_obj.gif",
              new IModification()
              {
                public void apply(IModificationContext context) throws BadLocationException
                {
                  IXtextDocument xtextDocument = context.getXtextDocument();
                  xtextDocument.replace(removalRegion.deleteBegin, removalRegion.deleteEnd- removalRegion.deleteBegin, removalRegion.replacement);
                }
              });
         }
       });
  }

  @Fix(XcoreIssueCodes.WILDCARD_IMPORT)
  public void oranizeImports(final Issue issue, final IssueResolutionAcceptor acceptor)
  {
    IModificationContext modificationContext = getModificationContextFactory().createModificationContext(issue);
    final IXtextDocument xtextDocument = modificationContext.getXtextDocument();
    xtextDocument.readOnly
      (new IUnitOfWork.Void<XtextResource>()
       {
         @Override
         public void process(XtextResource xtextResource) throws Exception
         {
           XcoreImportOrganizer xcoreImportOrganizer = xcoreImportOrganizerProvider.get();
           final TextRegion importRegion = xcoreImportOrganizer.getImportRegion(xtextResource);
           final String importSection = xcoreImportOrganizer.getOrganizedImportSection(xtextResource);
           acceptor.accept
             (issue,
              "Organize all imports",
              importSection.trim(),
              "full/obj16/correction_change.gif",
              new IModification()
              {
                public void apply(IModificationContext context) throws BadLocationException
                {
                  IXtextDocument xtextDocument = context.getXtextDocument();
                  int offset = importRegion.getOffset();
                  int length = importRegion.getLength();
                  String string = xtextDocument.get(offset, length);
                  if (!string.equals(importSection))
                  {
                    xtextDocument.replace(offset, length, importSection);
                  }
                }
              });
         }
       });
  }

  @Fix(XcoreIssueCodes.XBASE_LIB_NOT_ON_CLASSPATH)
  public void addXbaseLibToClasspath(final Issue issue, final IssueResolutionAcceptor acceptor)
  {
    acceptor.accept
      (issue,
       "Add Xbase library to classpath",
       "Add 'org.eclipse.xtext.xbase.lib' to the project's classpath",
       "full/obj16/correction_change.gif",
       new ISemanticModification()
       {
         public void apply(EObject element, IModificationContext context) throws Exception
         {
           ResourceSet resourceSet = element.eResource().getResourceSet();
           IJavaProject javaProject = projectProvider.getJavaProject(resourceSet);
           classpathUpdater.addBundle(javaProject, "org.eclipse.xtext.xbase.lib", new NullProgressMonitor());
         }
       });

  }

  @Fix(XcoreIssueCodes.XCORE_LIB_NOT_ON_CLASSPATH)
  public void addXcoreLibToClasspath(final Issue issue, final IssueResolutionAcceptor acceptor)
  {
    acceptor.accept
      (issue,
       "Add Xcore library to classpath",
       "Add 'org.eclipse.emf.ecore.xcore.lib' to the project's classpath",
       "full/obj16/correction_change.gif",
       new ISemanticModification()
       {
         public void apply(EObject element, IModificationContext context) throws Exception
         {
           ResourceSet resourceSet = element.eResource().getResourceSet();
           IJavaProject javaProject = projectProvider.getJavaProject(resourceSet);
           classpathUpdater.addBundle(javaProject, "org.eclipse.emf.ecore.xcore.lib", new NullProgressMonitor());
         }
       });
  }
}
