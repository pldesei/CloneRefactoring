/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.ecore.xcore.interpreter;


import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.XBlockExpression;
import org.eclipse.xtext.xbase.interpreter.IEvaluationResult;
import org.eclipse.xtext.xbase.interpreter.impl.DefaultEvaluationContext;

import com.google.inject.Inject;


public class XcoreConversionDelegate implements EDataType.Internal.ConversionDelegate
{
  @Inject
  private XcoreInterpreter interpreter;

  private XBlockExpression createBody;

  private XBlockExpression convertBody;

  private EDataType eDataType;

  public void initialize(XBlockExpression createBody, XBlockExpression convertBody, EDataType eDataType, XcoreInterpreter interpreter)
  {
    this.createBody = createBody;
    this.convertBody = convertBody;
    this.eDataType = eDataType;
    this.interpreter = interpreter;
  }

  public String convertToString(Object value)
  {
    if (convertBody == null)
    {
      return EcoreUtil.convertToString(eDataType, value);
    }
    else
    {
      DefaultEvaluationContext context = new DefaultEvaluationContext();
      context.newValue(QualifiedName.create("it"), value);
  
      IEvaluationResult result = interpreter.evaluate(convertBody, context, CancelIndicator.NullImpl);
      if (result.getException() != null)
      {
        throw new RuntimeException(result.getException());
      }
      else
      {
        return (String)result.getResult();
      }
    }
  }

  public Object createFromString(String literal)
  {
    if (createBody == null)
    {
      return EcoreUtil.createFromString(eDataType, literal);
    }
    else
    {
      DefaultEvaluationContext context = new DefaultEvaluationContext();
      context.newValue(QualifiedName.create("it"), literal);
      IEvaluationResult result = interpreter.evaluate(createBody, context, CancelIndicator.NullImpl);
      if (result.getException() != null)
      {
        throw new RuntimeException(result.getException());
      }
      else
      {
        return result.getResult();
      }
    }
  }
}
