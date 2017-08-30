/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.ecore.xcore.generator;


import org.eclipse.xtext.common.types.JvmArrayType;
import org.eclipse.xtext.common.types.JvmPrimitiveType;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeParameter;
import org.eclipse.xtext.common.types.JvmVoid;
import org.eclipse.xtext.xbase.compiler.ImportManager;


public class XcoreImportManager extends ImportManager
{
  public XcoreImportManager()
  {
    super(false);
  }

  @Override
  public void appendType(JvmType type, StringBuilder builder)
  {
    if (type instanceof JvmPrimitiveType || type instanceof JvmVoid || type instanceof JvmTypeParameter)
    {
      builder.append(type.getQualifiedName('.'));
    }
    else if (type instanceof JvmArrayType)
    {
      appendType(((JvmArrayType)type).getComponentType(), builder);
      builder.append("[]");
    }
    else
    {
      final String qualifiedName = type.getQualifiedName('.');
      builder.append("<%");
      builder.append(qualifiedName);
      builder.append("%>");
    }
  }
}
