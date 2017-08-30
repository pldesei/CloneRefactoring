/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.edit.provider;



/**
 * This is implemented by an item provider that decorates another item provider.
 */
public interface IItemProviderDecorator 
{
  /**
   * This returns the decorated item provider, which must implement the {@link org.eclipse.emf.edit.provider.IChangeNotifier} interface.
   */
  IChangeNotifier getDecoratedItemProvider();

  /**
   * This set the decorated item provider, which must implement the {@link org.eclipse.emf.edit.provider.IChangeNotifier} interface.
   */
  void setDecoratedItemProvider(IChangeNotifier decoratedItemProvider);
}
