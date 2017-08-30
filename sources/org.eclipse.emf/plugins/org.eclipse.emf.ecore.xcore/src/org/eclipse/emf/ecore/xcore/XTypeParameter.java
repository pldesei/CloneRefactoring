/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.ecore.xcore;


import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XType Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.xcore.XTypeParameter#getBounds <em>Bounds</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.xcore.XcorePackage#getXTypeParameter()
 * @model
 * @generated
 */
public interface XTypeParameter extends XNamedElement
{
  /**
   * Returns the value of the '<em><b>Bounds</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.xcore.XGenericType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Bounds</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Bounds</em>' containment reference list.
   * @see org.eclipse.emf.ecore.xcore.XcorePackage#getXTypeParameter_Bounds()
   * @model containment="true"
   * @generated
   */
  EList<XGenericType> getBounds();

} // XTypeParameter
