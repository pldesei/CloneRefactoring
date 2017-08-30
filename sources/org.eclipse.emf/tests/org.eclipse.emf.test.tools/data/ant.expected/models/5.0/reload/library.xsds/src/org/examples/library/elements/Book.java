/**
 * This is my code.
 */
package org.examples.library.elements;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Book</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.examples.library.elements.Book#getTitle <em>Title</em>}</li>
 *   <li>{@link org.examples.library.elements.Book#getPages <em>Pages</em>}</li>
 *   <li>{@link org.examples.library.elements.Book#getCategory <em>Category</em>}</li>
 *   <li>{@link org.examples.library.elements.Book#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.examples.library.elements.Book#getUuid <em>Uuid</em>}</li>
 * </ul>
 *
 * @see org.examples.library.elements.ElementsPackage#getBook()
 * @model extendedMetaData="name='Book' kind='elementOnly'"
 * @generated
 */
public interface Book extends EObject
{
  /**
   * Returns the value of the '<em><b>Title</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Title</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Title</em>' attribute.
   * @see #setTitle(String)
   * @see org.examples.library.elements.ElementsPackage#getBook_Title()
   * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
   *        extendedMetaData="kind='element' name='title'"
   * @generated
   */
  String getTitle();

  /**
   * Sets the value of the '{@link org.examples.library.elements.Book#getTitle <em>Title</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Title</em>' attribute.
   * @see #getTitle()
   * @generated
   */
  void setTitle(String value);

  /**
   * Returns the value of the '<em><b>Pages</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Pages</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Pages</em>' attribute.
   * @see #isSetPages()
   * @see #unsetPages()
   * @see #setPages(int)
   * @see org.examples.library.elements.ElementsPackage#getBook_Pages()
   * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int" required="true"
   *        extendedMetaData="kind='element' name='pages'"
   * @generated
   */
  int getPages();

  /**
   * Sets the value of the '{@link org.examples.library.elements.Book#getPages <em>Pages</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Pages</em>' attribute.
   * @see #isSetPages()
   * @see #unsetPages()
   * @see #getPages()
   * @generated
   */
  void setPages(int value);

  /**
   * Unsets the value of the '{@link org.examples.library.elements.Book#getPages <em>Pages</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSetPages()
   * @see #getPages()
   * @see #setPages(int)
   * @generated
   */
  void unsetPages();

  /**
   * Returns whether the value of the '{@link org.examples.library.elements.Book#getPages <em>Pages</em>}' attribute is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>Pages</em>' attribute is set.
   * @see #unsetPages()
   * @see #getPages()
   * @see #setPages(int)
   * @generated
   */
  boolean isSetPages();

  /**
   * Returns the value of the '<em><b>Category</b></em>' attribute.
   * The literals are from the enumeration {@link org.examples.library.elements.BookCategory}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Category</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Category</em>' attribute.
   * @see org.examples.library.elements.BookCategory
   * @see #isSetCategory()
   * @see #unsetCategory()
   * @see #setCategory(BookCategory)
   * @see org.examples.library.elements.ElementsPackage#getBook_Category()
   * @model unsettable="true" required="true"
   *        extendedMetaData="kind='element' name='category'"
   * @generated
   */
  BookCategory getCategory();

  /**
   * Sets the value of the '{@link org.examples.library.elements.Book#getCategory <em>Category</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Category</em>' attribute.
   * @see org.examples.library.elements.BookCategory
   * @see #isSetCategory()
   * @see #unsetCategory()
   * @see #getCategory()
   * @generated
   */
  void setCategory(BookCategory value);

  /**
   * Unsets the value of the '{@link org.examples.library.elements.Book#getCategory <em>Category</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSetCategory()
   * @see #getCategory()
   * @see #setCategory(BookCategory)
   * @generated
   */
  void unsetCategory();

  /**
   * Returns whether the value of the '{@link org.examples.library.elements.Book#getCategory <em>Category</em>}' attribute is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>Category</em>' attribute is set.
   * @see #unsetCategory()
   * @see #getCategory()
   * @see #setCategory(BookCategory)
   * @generated
   */
  boolean isSetCategory();

  /**
   * Returns the value of the '<em><b>Author</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Author</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Author</em>' reference.
   * @see #setAuthor(EObject)
   * @see org.examples.library.elements.ElementsPackage#getBook_Author()
   * @model required="true"
   *        extendedMetaData="kind='element' name='author'"
   * @generated
   */
  EObject getAuthor();

  /**
   * Sets the value of the '{@link org.examples.library.elements.Book#getAuthor <em>Author</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Author</em>' reference.
   * @see #getAuthor()
   * @generated
   */
  void setAuthor(EObject value);

  /**
   * Returns the value of the '<em><b>Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Uuid</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Uuid</em>' attribute.
   * @see #setUuid(byte[])
   * @see org.examples.library.elements.ElementsPackage#getBook_Uuid()
   * @model dataType="org.examples.library.elements.UUID"
   *        extendedMetaData="kind='attribute' name='uuid'"
   * @generated
   */
  byte[] getUuid();

  /**
   * Sets the value of the '{@link org.examples.library.elements.Book#getUuid <em>Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Uuid</em>' attribute.
   * @see #getUuid()
   * @generated
   */
  void setUuid(byte[] value);

} // Book
