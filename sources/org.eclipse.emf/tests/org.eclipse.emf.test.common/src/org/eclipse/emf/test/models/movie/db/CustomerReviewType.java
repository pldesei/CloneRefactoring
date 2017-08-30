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
package org.eclipse.emf.test.models.movie.db;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Customer Review Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.test.models.movie.db.CustomerReviewType#getComment <em>Comment</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.test.models.movie.db.DBPackage#getCustomerReviewType()
 * @model extendedMetaData="name='customerReviewType' kind='elementOnly'"
 * @generated
 */
public interface CustomerReviewType extends CriticsReviewType
{
  /**
   * Returns the value of the '<em><b>Comment</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Comment</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Comment</em>' attribute.
   * @see #setComment(String)
   * @see org.eclipse.emf.test.models.movie.db.DBPackage#getCustomerReviewType_Comment()
   * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
   *        extendedMetaData="kind='element' name='comment'"
   * @generated
   */
  String getComment();

  /**
   * Sets the value of the '{@link org.eclipse.emf.test.models.movie.db.CustomerReviewType#getComment <em>Comment</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Comment</em>' attribute.
   * @see #getComment()
   * @generated
   */
  void setComment(String value);

} // CustomerReviewType
