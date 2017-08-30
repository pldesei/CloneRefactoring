/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.emf.ecore.xcore.scoping.types;

import java.util.List;

import org.eclipse.xtext.common.types.JvmPrimitiveType;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmVoid;
import org.eclipse.xtext.common.types.access.impl.Primitives;
import org.eclipse.xtext.common.types.xtext.AbstractTypeScope;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;

public class PrimitiveAwareScope extends AbstractXcoreScope
{
  private AbstractTypeScope typeScope;

  private AbstractXcoreScope parent;

  public PrimitiveAwareScope(AbstractXcoreScope parent, AbstractTypeScope typeScope)
  {
    this.parent = parent;
    this.typeScope = typeScope;
  }

  @Override
  public IEObjectDescription getSingleElement(QualifiedName name)
  {
    if (isPrimitive(name))
    {
      return typeScope.getSingleElement(name, true);
    }
    return parent.getSingleElement(name);
  }

  protected boolean isPrimitive(QualifiedName name)
  {
    return name.getSegmentCount() == 1 && Primitives.forName(name.getFirstSegment()) != null;
  }

  @Override
  protected void doGetElements(JvmType type, List<IEObjectDescription> result)
  {
    if (type instanceof JvmVoid)
    {
      result.add(EObjectDescription.create("void", type));
      return;
    }
    if (type instanceof JvmPrimitiveType)
    {
      result.add(EObjectDescription.create(((JvmPrimitiveType)type).getSimpleName(), type));
      return;
    }
    parent.doGetElements(type, result);
  }
}