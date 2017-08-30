/**
 * Copyright (c) 2009 BestSolution and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *   Tom Schindl - Initial API and implementation
 */
package org.eclipse.emf.test.databinding.emfdb;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>A</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.test.databinding.emfdb.A#getString <em>String</em>}</li>
 *   <li>{@link org.eclipse.emf.test.databinding.emfdb.A#getBlist <em>Blist</em>}</li>
 *   <li>{@link org.eclipse.emf.test.databinding.emfdb.A#getCmap <em>Cmap</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.test.databinding.emfdb.EmfdbPackage#getA()
 * @model
 * @generated
 */
public interface A extends EObject
{
  /**
   * Returns the value of the '<em><b>String</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>String</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>String</em>' attribute.
   * @see #setString(String)
   * @see org.eclipse.emf.test.databinding.emfdb.EmfdbPackage#getA_String()
   * @model
   * @generated
   */
  String getString();

  /**
   * Sets the value of the '{@link org.eclipse.emf.test.databinding.emfdb.A#getString <em>String</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>String</em>' attribute.
   * @see #getString()
   * @generated
   */
  void setString(String value);

  /**
   * Returns the value of the '<em><b>Blist</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.test.databinding.emfdb.B}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Blist</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Blist</em>' containment reference list.
   * @see org.eclipse.emf.test.databinding.emfdb.EmfdbPackage#getA_Blist()
   * @model containment="true"
   * @generated
   */
  EList<B> getBlist();

  /**
   * Returns the value of the '<em><b>Cmap</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link java.lang.String},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cmap</em>' map isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cmap</em>' map.
   * @see org.eclipse.emf.test.databinding.emfdb.EmfdbPackage#getA_Cmap()
   * @model mapType="org.eclipse.emf.test.databinding.emfdb.C<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
   * @generated
   */
  EMap<String, String> getCmap();

} // A
