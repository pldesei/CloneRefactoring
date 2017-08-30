/**
 * Copyright (c) 2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.ecore.xcore.mappings;


import org.eclipse.emf.codegen.ecore.genmodel.GenParameter;
import org.eclipse.emf.ecore.EParameter;


public class XParameterMapping extends AbstractMapping
{
  private GenParameter genParameter;

  private EParameter eParameter;

  public GenParameter getGenParameter()
  {
    return genParameter;
  }

  public void setGenParameter(GenParameter genParameter)
  {
    this.genParameter = genParameter;
  }

  public EParameter getEParameter()
  {
    return eParameter;
  }

  public void setEParameter(EParameter eParameter)
  {
    this.eParameter = eParameter;
  }
}
