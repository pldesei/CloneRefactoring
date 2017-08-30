/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.test.ecore.xcore.interpreter;


import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xcore.XPackage;
import org.eclipse.emf.test.ecore.xcore.XcoreStandaloneInjectorProvider;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.junit.evaluation.AbstractXbaseEvaluationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;


@RunWith(XtextRunner.class)
@InjectWith(XcoreStandaloneInjectorProvider.class)
public class XcoreInterpreterXbaseIntegrationTest extends AbstractXbaseEvaluationTest
{
  @Inject
  private ParseHelper<XPackage> parser;

  @Inject
  private ValidationTestHelper validator;

  @Override
  protected Object invokeXbaseExpression(String expression) throws Exception
  {
    XPackage pack = parser.parse("package foo type Exception wraps Exception class Bar { op Object foo() throws Exception { " + expression + " } }");
    validator.assertNoErrors(pack);
    EPackage ePack = (EPackage)pack.eResource().getContents().get(2);
    EClass barClass = (EClass)ePack.getEClassifier("Bar");
    EObject eObject = ePack.getEFactoryInstance().create(barClass);
    return eObject.eInvoke(barClass.getEOperations().get(0), new BasicEList<Object>());
  }

  @Override
  @Test public void testSwitchExpressionOverEnum_6() throws Exception {
		assertEvaluatesTo(null, 
				"{\n" + 
				"  val Enum<?> e = null\n" + 
				"  switch(e) {\n" + 
				"    java.lang.^annotation.RetentionPolicy: e.toString\n" + 
				"    java.lang.^annotation.ElementType: e.toString\n" + 
				"  }\n" + 
				"}");
	}

  @Override
  @Test 
  public void testReservedWordEnum() throws Exception 
  {
    assertEvaluatesTo(Boolean.TRUE, "typeof(java.lang.^annotation.RetentionPolicy).enum");
  }
  
  @Override
  @Test 
  public void testSwitchExpression_27() throws Exception {
	  // annotation is a reserved word in Xcore
	  assertEvaluatesTo(Boolean.FALSE, 
				"{ val Object policy = java.lang.^annotation.RetentionPolicy.SOURCE switch policy { java.lang.^annotation.RetentionPolicy case CLASS: false } }");
  }

  @Override
  @Test
  public void testArrays_01() throws Exception
  {
    try
    {
      super.testArrays_01();
      fail("Expecting an exception; it must be working now.");
    }
    catch (Exception exception)
    {
      // Expecting it to fail right now.
    }
  }

  @Override
  @Test
  public void testArrays_02() throws Exception
  {
    try
    {
      super.testArrays_02();
      fail("Expecting an exception; it must be working now.");
    }
    catch (Exception exception)
    {
      // Expecting it to fail right now.
    }
  }

  @Override
  @Test
  public void testArrays_04() throws Exception
  {
    try
    {
      super.testArrays_04();
      fail("Expecting an exception; it must be working now.");
    }
    catch (Exception exception)
    {
      // Expecting it to fail right now.
    }
  }

  @Override
  @Test
  public void testClosure_31() throws Exception
  {
    try
    {
      super.testClosure_31();

      fail("Expecting an exception; it must be working now.");
    }
    catch (Exception exception)
    {
      // Expecting it to fail right now.
    }
  }

  @Override
  public void testClosure_32() throws Exception
  {
    try
    {
      super.testClosure_32();

      fail("Expecting an exception; it must be working now.");
    }
    catch (Exception exception)
    {
      // Expecting it to fail right now.
    }
  }
}
