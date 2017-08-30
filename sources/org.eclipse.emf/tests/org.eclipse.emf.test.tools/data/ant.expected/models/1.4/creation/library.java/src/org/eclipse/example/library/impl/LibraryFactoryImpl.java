/**
 * This is my code.
 */
package org.eclipse.example.library.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.example.library.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LibraryFactoryImpl extends EFactoryImpl implements LibraryFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static LibraryFactory init()
  {
    try
    {
      LibraryFactory theLibraryFactory = (LibraryFactory)EPackage.Registry.INSTANCE.getEFactory(LibraryPackage.eNS_URI);
      if (theLibraryFactory != null)
      {
        return theLibraryFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new LibraryFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LibraryFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case LibraryPackage.BOOK: return createBook();
      case LibraryPackage.LIBRARY: return createLibrary();
      case LibraryPackage.WRITER: return createWriter();
      case LibraryPackage.ESTRING_TO_BOOK_MAP_ENTRY: return (EObject)createEStringToBookMapEntry();
      case LibraryPackage.ESTRING_TO_WRITER_MAP_ENTRY: return (EObject)createEStringToWriterMapEntry();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case LibraryPackage.BOOK_CATEGORY:
        return createBookCategoryFromString(eDataType, initialValue);
      case LibraryPackage.MY_MAP_OF_INTEGERS_AND_STRINGS:
        return createMyMapOfIntegersAndStringsFromString(eDataType, initialValue);
      case LibraryPackage.MAP:
        return createMapFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case LibraryPackage.BOOK_CATEGORY:
        return convertBookCategoryToString(eDataType, instanceValue);
      case LibraryPackage.MY_MAP_OF_INTEGERS_AND_STRINGS:
        return convertMyMapOfIntegersAndStringsToString(eDataType, instanceValue);
      case LibraryPackage.MAP:
        return convertMapToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Book createBook()
  {
    BookImpl book = new BookImpl();
    return book;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Library createLibrary()
  {
    LibraryImpl library = new LibraryImpl();
    return library;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Writer createWriter()
  {
    WriterImpl writer = new WriterImpl();
    return writer;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry createEStringToBookMapEntry()
  {
    EStringToBookMapEntryImpl eStringToBookMapEntry = new EStringToBookMapEntryImpl();
    return eStringToBookMapEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry createEStringToWriterMapEntry()
  {
    EStringToWriterMapEntryImpl eStringToWriterMapEntry = new EStringToWriterMapEntryImpl();
    return eStringToWriterMapEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BookCategory createBookCategoryFromString(EDataType eDataType, String initialValue)
  {
    BookCategory result = BookCategory.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertBookCategoryToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map createMyMapOfIntegersAndStringsFromString(EDataType eDataType, String initialValue)
  {
    return (Map)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertMyMapOfIntegersAndStringsToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map createMapFromString(EDataType eDataType, String initialValue)
  {
    return (Map)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertMapToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LibraryPackage getLibraryPackage()
  {
    return (LibraryPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  public static LibraryPackage getPackage()
  {
    return LibraryPackage.eINSTANCE;
  }

} //LibraryFactoryImpl
