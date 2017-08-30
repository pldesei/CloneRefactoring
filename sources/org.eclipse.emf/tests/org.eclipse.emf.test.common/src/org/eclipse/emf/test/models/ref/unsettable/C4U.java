/**
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved.  This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.test.models.ref.unsettable;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>C4U</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.test.models.ref.unsettable.C4U#getCu <em>Cu</em>}</li>
 *   <li>{@link org.eclipse.emf.test.models.ref.unsettable.C4U#getDu <em>Du</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.test.models.ref.unsettable.URefPackage#getC4U()
 * @model
 * @generated
 */
public interface C4U extends EObject
{
  /**
   * Returns the value of the '<em><b>Cu</b></em>' containment reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.test.models.ref.unsettable.CU#getC4u <em>C4u</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cu</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cu</em>' containment reference.
   * @see #isSetCu()
   * @see #unsetCu()
   * @see #setCu(CU)
   * @see org.eclipse.emf.test.models.ref.unsettable.URefPackage#getC4U_Cu()
   * @see org.eclipse.emf.test.models.ref.unsettable.CU#getC4u
   * @model opposite="c4u" containment="true" unsettable="true" required="true"
   * @generated
   */
  CU getCu();

  /**
   * Sets the value of the '{@link org.eclipse.emf.test.models.ref.unsettable.C4U#getCu <em>Cu</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Cu</em>' containment reference.
   * @see #isSetCu()
   * @see #unsetCu()
   * @see #getCu()
   * @generated
   */
  void setCu(CU value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.test.models.ref.unsettable.C4U#getCu <em>Cu</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSetCu()
   * @see #getCu()
   * @see #setCu(CU)
   * @generated
   */
  void unsetCu();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.test.models.ref.unsettable.C4U#getCu <em>Cu</em>}' containment reference is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>Cu</em>' containment reference is set.
   * @see #unsetCu()
   * @see #getCu()
   * @see #setCu(CU)
   * @generated
   */
  boolean isSetCu();

  /**
   * Returns the value of the '<em><b>Du</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.test.models.ref.unsettable.DU}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.test.models.ref.unsettable.DU#getC4u <em>C4u</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Du</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Du</em>' containment reference list.
   * @see #isSetDu()
   * @see #unsetDu()
   * @see org.eclipse.emf.test.models.ref.unsettable.URefPackage#getC4U_Du()
   * @see org.eclipse.emf.test.models.ref.unsettable.DU#getC4u
   * @model opposite="c4u" containment="true" unsettable="true"
   * @generated
   */
  EList<DU> getDu();

  /**
   * Unsets the value of the '{@link org.eclipse.emf.test.models.ref.unsettable.C4U#getDu <em>Du</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSetDu()
   * @see #getDu()
   * @generated
   */
  void unsetDu();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.test.models.ref.unsettable.C4U#getDu <em>Du</em>}' containment reference list is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>Du</em>' containment reference list is set.
   * @see #unsetDu()
   * @see #getDu()
   * @generated
   */
  boolean isSetDu();

} // C4U
