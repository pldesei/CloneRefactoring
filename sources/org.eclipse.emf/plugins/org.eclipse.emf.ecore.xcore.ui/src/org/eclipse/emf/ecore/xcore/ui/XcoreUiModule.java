/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.ecore.xcore.ui;


import org.eclipse.emf.ecore.xcore.formatting.XcoreFormatterPreferenceValuesProvider;
import org.eclipse.emf.ecore.xcore.interpreter.IClassLoaderProvider;
import org.eclipse.emf.ecore.xcore.ui.builder.XcoreBuildParticipant;
import org.eclipse.emf.ecore.xcore.ui.builder.XcoreFileSystemAccess;
import org.eclipse.emf.ecore.xcore.ui.builder.XcoreWorkingCopyOwnerProvider;
import org.eclipse.emf.ecore.xcore.ui.container.XcoreJavaProjectsState;
import org.eclipse.emf.ecore.xcore.ui.contentassist.ImportingTypesProposalProvider;
import org.eclipse.emf.ecore.xcore.ui.contentassist.PartialParsingContentAssistParser;
import org.eclipse.emf.ecore.xcore.ui.contentassist.XcoreVariableCompletions;
import org.eclipse.emf.ecore.xcore.ui.formatting.XcoreFormatterFactory;
import org.eclipse.emf.ecore.xcore.ui.hover.XcoreHoverDocumentationProvider;
import org.eclipse.emf.ecore.xcore.ui.hover.XcoreHoverProvider;
import org.eclipse.emf.ecore.xcore.ui.hover.XcoreHoverSignatureProvider;
import org.eclipse.emf.ecore.xcore.ui.hyperlinking.XcoreHyperLinkHelper;
import org.eclipse.emf.ecore.xcore.ui.refactoring.XcoreDependentElementsCalculator;
import org.eclipse.emf.ecore.xcore.ui.refactoring.XcoreJavaElementFinder;
import org.eclipse.emf.ecore.xcore.ui.refactoring.XcoreRenameElementProcessor;
import org.eclipse.emf.ecore.xcore.ui.refactoring.XcoreRenameRefactoringParticipantProcessor;
import org.eclipse.emf.ecore.xcore.ui.refactoring.XcoreRenameStrategy;
import org.eclipse.emf.ecore.xcore.ui.validation.XcoreUniqueClassNameValidator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.common.types.access.jdt.IWorkingCopyOwnerProvider;
import org.eclipse.xtext.common.types.ui.refactoring.JdtRenameRefactoringProcessorFactory;
import org.eclipse.xtext.common.types.ui.refactoring.participant.JdtRenameParticipant.ContextFactory;
import org.eclipse.xtext.common.types.util.jdt.IJavaElementFinder;
import org.eclipse.xtext.common.types.xtext.ui.ITypesProposalProvider;
import org.eclipse.xtext.common.types.xtext.ui.JdtVariableCompletions;
import org.eclipse.xtext.formatting2.FormatterPreferenceValuesProvider;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.ui.containers.JavaProjectsState;
import org.eclipse.xtext.ui.editor.contentassist.antlr.IContentAssistParser;
import org.eclipse.xtext.ui.editor.formatting.IContentFormatterFactory;
import org.eclipse.xtext.ui.editor.hover.IEObjectHover;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hover.html.IEObjectHoverDocumentationProvider;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkHelper;
import org.eclipse.xtext.ui.refactoring.IDependentElementsCalculator;
import org.eclipse.xtext.ui.refactoring.IRenameStrategy;
import org.eclipse.xtext.ui.refactoring.impl.RenameElementProcessor;
import org.eclipse.xtext.xbase.ui.hover.XbaseDeclarativeHoverSignatureProvider;
import org.eclipse.xtext.xbase.ui.hover.XbaseDispatchingEObjectTextHover;
import org.eclipse.xtext.xbase.ui.jvmmodel.refactoring.jdt.JdtRenameRefactoringParticipantProcessor;
import org.eclipse.xtext.xbase.validation.UniqueClassNameValidator;

import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * Use this class to register components to be used within the IDE.
 */
public class XcoreUiModule extends AbstractXcoreUiModule
{
  public XcoreUiModule(AbstractUIPlugin plugin)
  {
    super(plugin);
  }

  @Override
  public Class<? extends IHyperlinkHelper> bindIHyperlinkHelper()
  {
    return XcoreHyperLinkHelper.class;
  }

  @Override
  public Class<? extends ITypesProposalProvider> bindITypesProposalProvider()
  {
    return ImportingTypesProposalProvider.class;
  }

  @Override
  public Class<? extends IEObjectHoverProvider> bindIEObjectHoverProvider()
  {
    return XcoreHoverProvider.class;
  }

  public Class<? extends RenameElementProcessor> bindRenameElementProcessor()
  {
    return XcoreRenameElementProcessor.class;
  }

  @Override
  public Class<? extends IRenameStrategy> bindIRenameStrategy()
  {
    return XcoreRenameStrategy.class;
  }

  @Override
  public Class<? extends IDependentElementsCalculator> bindIDependentElementsCalculator()
  {
    return XcoreDependentElementsCalculator.class;
  }

  @Override
  public Class<? extends IJavaProjectProvider> bindIJavaProjectProvider()
  {
    return XcoreJavaProjectProvider.class;
  }

  public Class<?extends EclipseResourceFileSystemAccess2> bindEclipseResourceFileSystemAccess2()
  {
    return XcoreFileSystemAccess.class;
  }

  @Override
  public Class<? extends IXtextBuilderParticipant> bindIXtextBuilderParticipant()
  {
    return XcoreBuildParticipant.class;
  }

  public Class<? extends IClassLoaderProvider> bindIClassLoaderProvider()
  {
    return XcoreJavaProjectProvider.class;
  }

  @Override
  public Class<? extends IEObjectHover> bindIEObjectHover()
  {
    return XbaseDispatchingEObjectTextHover.class;
  }

  public Class<? extends XbaseDeclarativeHoverSignatureProvider> bindXbaseDeclarativeHoverSignatureProvider()
  {
    return XcoreHoverSignatureProvider.class;
  }

  public Class<? extends JdtVariableCompletions> bindJdtVariableCompletions()
  {
    return XcoreVariableCompletions.class;
  }

  public Class<? extends IJavaElementFinder> bindIJavaElementFinder()
  {
    return XcoreJavaElementFinder.class;
  }

  public Class<? extends JdtRenameRefactoringProcessorFactory> bindJdtRenameRefactoringProcessorFactory()
  {
    return JdtRenameRefactoringProcessorFactory.class;
  }

  @Override
  public Provider<IAllContainersState> provideIAllContainersState()
  {
   return
     new Provider<IAllContainersState>()
     {
       @Inject
       XcoreJavaProjectsState instance;

       public IAllContainersState get()
       {
         return instance;
       }
     };
  }

  public Class<? extends JavaProjectsState> bindJavaProjectState()
  {
    return XcoreJavaProjectsState.class;
  }

  @Override
  public Class<? extends IEObjectHoverDocumentationProvider> bindIEObjectHoverDocumentationProvider()
  {
    return XcoreHoverDocumentationProvider.class;
  }

  public Class<? extends IWorkingCopyOwnerProvider> bindIWorkingCopyOwnerProvider()
  {
    return XcoreWorkingCopyOwnerProvider.class;
  }

  @Override
  public Class<? extends IContentFormatterFactory> bindIContentFormatterFactory()
  {
    return XcoreFormatterFactory.class;
  }

  
  public Class<? extends FormatterPreferenceValuesProvider> bindFormatterPreferenceValuesProvider()
  {
    return XcoreFormatterPreferenceValuesProvider.class;
  }

  public Class<? extends JdtRenameRefactoringParticipantProcessor> bindJdtRenameRefactoringParticipantProcessor()
  {
    return XcoreRenameRefactoringParticipantProcessor.class;
  }

  @Override
  public Class<? extends ContextFactory> bindJdtRenameParticipant$ContextFactory()
  {
    return ContextFactory.class;
  }

  @Override
  public Class<? extends IContentAssistParser> bindIContentAssistParser()
  {
    return PartialParsingContentAssistParser.class;
  }

  @SingletonBinding(eager = true)
  @Override
  public Class<? extends UniqueClassNameValidator> bindUniqueClassNameValidator()
  {
    return XcoreUniqueClassNameValidator.class;
  }
}
