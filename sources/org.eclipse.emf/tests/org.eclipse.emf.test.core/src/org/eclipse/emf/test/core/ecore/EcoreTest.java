/**
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.test.core.ecore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Test;

public class EcoreTest
{
  public static class NotificationCollector extends AdapterImpl
  {
    private List<Notification> notifications = new ArrayList<Notification>();

    public List<Notification> getNotifications()
    {
      return notifications;
    }

    @Override
    public void notifyChanged(Notification msg)
    {
      notifications.add(msg);
    }
  }

  /*
   * <a href="http://bugs.eclipse.org/169926">Bug 169926</a>
   * This must be run before any other tests using Ecore, since it will always pass if EcorePackage has been initialized.
   */
  @Test
  public void testCreateAnnotationOnInitialization()
  {
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource("XTest");
    annotation.getDetails().put("Test", "true");
    assertEquals("true", annotation.getDetails().get("Test"));
  }

  /*
   * <a href="http://bugs.eclipse.org/170549">Bugzilla 170549</a>
   */
  @Test
  public void testESuperTypeNotificationCount() throws Exception
  {
    EClass eClass1 = EcoreFactory.eINSTANCE.createEClass();
    eClass1.setName("Class1");
    EClass eClass2 = EcoreFactory.eINSTANCE.createEClass();
    eClass2.setName("Class2");

    NotificationCollector notificationCollector = new NotificationCollector();
    eClass1.eAdapters().add(notificationCollector);
    eClass2.eAdapters().add(notificationCollector);

    eClass2.getESuperTypes().add(eClass1);

    assertEquals(2, notificationCollector.getNotifications().size());
  }

  /**
   * <a href="http://bugs.eclipse.org/212903">Bugzilla 212903</a>
   */
  @Test
  public void testESuperTypeLastIndexOf() throws Exception
  {
    EClass eClass1 = EcoreFactory.eINSTANCE.createEClass();
    eClass1.setName("Class1");
    EClass eClass2 = EcoreFactory.eINSTANCE.createEClass();
    eClass2.setName("Class2");

    eClass2.getESuperTypes().add(eClass1);

    assertEquals(0, eClass2.getESuperTypes().lastIndexOf(eClass1));
  }

  /*
   * <a href="http://bugs.eclipse.org/170549">Bugzilla 170549</a>
   */
  @Test
  public void testEExceptionNotificationCount() throws Exception
  {
    EOperation eOperation = EcoreFactory.eINSTANCE.createEOperation();
    eOperation.setName("operation");
    EClass aException = EcoreFactory.eINSTANCE.createEClass();
    aException.setName("AException");

    NotificationCollector notificationCollector = new NotificationCollector();
    eOperation.eAdapters().add(notificationCollector);
    aException.eAdapters().add(notificationCollector);

    eOperation.getEExceptions().add(aException);

    assertEquals(2, notificationCollector.getNotifications().size());
  }

  /**
   * <a href="http://bugs.eclipse.org/235992">Bugzilla 235992</a>
   */
  @Test
  public void testFrozenModelChange()
  {
    try
    {
      assert false;
      fail("Assertions must be enabled via JVM flag -ea or -enableassertions");
    }
    catch (AssertionError exception)
    {
      // Ignore
    }
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    EcoreUtil.freeze(ePackage);
    boolean assertionFailure;
    try
    {
      new ResourceImpl().getContents().add(ePackage);
      assertionFailure = false;
    }
    catch (AssertionError exception)
    {
      assertionFailure = true;
    }
    assertTrue(assertionFailure);
  }
}
