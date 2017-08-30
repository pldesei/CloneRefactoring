/**
 * Copyright (c) 2006-2012 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.test.core.featuremap.supplier.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.test.core.featuremap.supplier.PurchaseOrder;
import org.eclipse.emf.test.core.featuremap.supplier.Supplier;
import org.eclipse.emf.test.core.featuremap.supplier.SupplierFactory;
import org.eclipse.emf.test.core.featuremap.supplier.SupplierPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SupplierPackageImpl extends EPackageImpl implements SupplierPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass purchaseOrderEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass supplierEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.test.core.featuremap.supplier.SupplierPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private SupplierPackageImpl()
  {
    super(eNS_URI, SupplierFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link SupplierPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static SupplierPackage init()
  {
    if (isInited) return (SupplierPackage)EPackage.Registry.INSTANCE.getEPackage(SupplierPackage.eNS_URI);

    // Obtain or create and register package
    SupplierPackageImpl theSupplierPackage = (SupplierPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SupplierPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new SupplierPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    XMLTypePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theSupplierPackage.createPackageContents();

    // Initialize created meta-data
    theSupplierPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theSupplierPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(SupplierPackage.eNS_URI, theSupplierPackage);
    return theSupplierPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPurchaseOrder()
  {
    return purchaseOrderEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPurchaseOrder_Id()
  {
    return (EAttribute)purchaseOrderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSupplier()
  {
    return supplierEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSupplier_Name()
  {
    return (EAttribute)supplierEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSupplier_Orders()
  {
    return (EAttribute)supplierEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSupplier_PreferredOrders()
  {
    return (EReference)supplierEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSupplier_StandardOrders()
  {
    return (EReference)supplierEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SupplierFactory getSupplierFactory()
  {
    return (SupplierFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    purchaseOrderEClass = createEClass(PURCHASE_ORDER);
    createEAttribute(purchaseOrderEClass, PURCHASE_ORDER__ID);

    supplierEClass = createEClass(SUPPLIER);
    createEAttribute(supplierEClass, SUPPLIER__NAME);
    createEAttribute(supplierEClass, SUPPLIER__ORDERS);
    createEReference(supplierEClass, SUPPLIER__PREFERRED_ORDERS);
    createEReference(supplierEClass, SUPPLIER__STANDARD_ORDERS);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes and features; add operations and parameters
    initEClass(purchaseOrderEClass, PurchaseOrder.class, "PurchaseOrder", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPurchaseOrder_Id(), theXMLTypePackage.getString(), "id", null, 1, 1, PurchaseOrder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(supplierEClass, Supplier.class, "Supplier", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSupplier_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, Supplier.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSupplier_Orders(), ecorePackage.getEFeatureMapEntry(), "orders", null, 0, -1, Supplier.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSupplier_PreferredOrders(), this.getPurchaseOrder(), null, "preferredOrders", null, 0, -1, Supplier.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getSupplier_StandardOrders(), this.getPurchaseOrder(), null, "standardOrders", null, 0, -1, Supplier.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
    createExtendedMetaDataAnnotations();
  }

  /**
   * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createExtendedMetaDataAnnotations()
  {
    String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";	
    addAnnotation
      (purchaseOrderEClass, 
       source, 
       new String[] 
       {
       "name", "PurchaseOrder",
       "kind", "elementOnly"
       });	
    addAnnotation
      (getPurchaseOrder_Id(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "id"
       });	
    addAnnotation
      (supplierEClass, 
       source, 
       new String[] 
       {
       "name", "Supplier",
       "kind", "elementOnly"
       });	
    addAnnotation
      (getSupplier_Name(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "name"
       });	
    addAnnotation
      (getSupplier_Orders(), 
       source, 
       new String[] 
       {
       "kind", "group",
       "name", "orders:1"
       });	
    addAnnotation
      (getSupplier_PreferredOrders(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "preferredOrders",
       "group", "#orders:1"
       });	
    addAnnotation
      (getSupplier_StandardOrders(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "standardOrders",
       "group", "#orders:1"
       });
  }

} //SupplierPackageImpl
