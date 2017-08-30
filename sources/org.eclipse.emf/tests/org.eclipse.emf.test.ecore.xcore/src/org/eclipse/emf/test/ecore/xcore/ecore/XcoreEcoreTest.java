/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.test.ecore.xcore.ecore;


import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xcore.XNamedElement;
import org.eclipse.emf.ecore.xcore.mappings.XcoreMapper;
import org.eclipse.emf.test.ecore.xcore.GenModelFormatter;
import org.eclipse.emf.test.ecore.xcore.XcoreStandaloneInjectorProvider;
import org.eclipse.emf.test.ecore.xcore.legacy_xpect_runner.InjectParameter;
import org.eclipse.emf.test.ecore.xcore.legacy_xpect_runner.Offset;
import org.eclipse.emf.test.ecore.xcore.legacy_xpect_runner.ParameterizedXtextRunner;
import org.eclipse.emf.test.ecore.xcore.legacy_xpect_runner.ResourceURIs;
import org.eclipse.emf.test.ecore.xcore.legacy_xpect_runner.XpectString;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;


@InjectWith(XcoreStandaloneInjectorProvider.class)
@RunWith(ParameterizedXtextRunner.class)
@ResourceURIs(baseDir = "test-models/org/eclipse/emf/test/ecore/xcore/ecore", fileExtensions = "xcore")
public class XcoreEcoreTest
{

  @Inject
  private XcoreMapper mapper;

  @InjectParameter
  private Offset offset;

  @InjectParameter
  private XtextResource resource;

  @Inject
  private ValidationTestHelper validationHelper;

  @Test
  public void noValidationIssues()
  {
    validationHelper.assertNoIssues(resource.getContents().get(0));
  }

  @XpectString
  public String eNamedElement()
  {
    EcoreUtil.resolveAll(resource);
    ENamedElement gen = mapper.getEcore((XNamedElement)offset.getEObject());
    gen.getEAnnotations().clear();
    return new GenModelFormatter().resolveCrossReferences().format(gen);
  }

}
