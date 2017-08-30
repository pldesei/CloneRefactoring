/**
 * Copyright (c) 2010-2011 Ed Merks and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Ed Merks - Initial API and implementation
 */
package org.eclipse.emf.test.core.change;


import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Assert;
import org.junit.Test;


public class ChangeDescriptionReverseTest
{
  public static abstract class TestHelper
  {
    void doit() throws Exception
    {
      ResourceSet originalResourceSet = new ResourceSetImpl();

      loadResources(originalResourceSet);

      Map<EObject, URI> eObjectToProxyURIMap = new HashMap<EObject, URI>();
      ChangeRecorder changeRecorder = new ChangeRecorder();
      changeRecorder.setRecordingTransientFeatures(false);
      changeRecorder.setEObjectToProxyURIMap(eObjectToProxyURIMap);
      changeRecorder.beginRecording(Collections.singleton(originalResourceSet));

      makeChanges();

      ChangeDescription changeDescription = changeRecorder.endRecording();
      changeDescription.copyAndReverse(eObjectToProxyURIMap);

      ResourceSet finalResourceSet = new ResourceSetImpl();
      Resource changeDescriptionResource = finalResourceSet.createResource(URI.createURI("changes.change"));
      changeDescriptionResource.getContents().add(changeDescription);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      changeDescriptionResource.save(out, null);
      changeDescriptionResource.getContents().clear();
      changeDescriptionResource.unload();
      changeDescriptionResource.load(new ByteArrayInputStream(out.toByteArray()), null);

      ChangeDescription finalChangeDescription = (ChangeDescription)changeDescriptionResource.getContents().get(0);
      finalChangeDescription.apply();

      finalResourceSet.getResources().remove(0);

      assertEquals(originalResourceSet, finalResourceSet);
    }

    abstract void loadResources(ResourceSet resourceSet);
    abstract void makeChanges();
  }

  @Test
  public void testOne() throws Exception
  {
    new TestHelper()
    {
      Resource ecoreResource;

      @Override
      void loadResources(ResourceSet resourceSet)
      {
        ecoreResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/Ecore.ecore"), true);
      }

      @Override
      void makeChanges()
      {
        EPackage ePackage = (EPackage)ecoreResource.getContents().get(0);
        ePackage.getEClassifiers().remove(1);
      }
    }.doit();
  }

  @Test
  public void testTwo() throws Exception
  {
    new TestHelper()
    {
      Resource ecoreResource;

      @Override
      void loadResources(ResourceSet resourceSet)
      {
        ecoreResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/Ecore.ecore"), true);
      }

      @Override
      void makeChanges()
      {
        EPackage ePackage = (EPackage)ecoreResource.getContents().get(0);
        EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        ePackage.getEClassifiers().add(5, eClass);
        eClass.setName("NewClass");
        EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
        eClass.getEAnnotations().add(eAnnotation);
        eAnnotation.getContents().add(0, ePackage.getEClassifiers().get(2));
      }
    }.doit();
  }

  @Test
  public void testThree() throws Exception
  {
    new TestHelper()
    {
      Resource ecoreResource;

      @Override
      void loadResources(ResourceSet resourceSet)
      {
        ecoreResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/Ecore.ecore"), true);
      }

      @Override
      void makeChanges()
      {
        EPackage ePackage = (EPackage)ecoreResource.getContents().get(0);
        ePackage.getEClassifiers().move(10, 5);
      }
    }.doit();
  }

  @Test
  public void testFour() throws Exception
  {
    new TestHelper()
    {
      Resource ecoreResource;
      Resource xmlTypeResource;

      @Override
      void loadResources(ResourceSet resourceSet)
      {
        ecoreResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/Ecore.ecore"), true);
        xmlTypeResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/XMLType.ecore"), true);
      }

      @Override
      void makeChanges()
      {
        EPackage ecorePackage = (EPackage)ecoreResource.getContents().get(0);
        EPackage xmlTypePackage = (EPackage)xmlTypeResource.getContents().get(0);
        xmlTypePackage.getEClassifiers().add(2, ecorePackage.getEClassifiers().get(2));
      }
    }.doit();
  }

  @Test
  public void testFive() throws Exception
  {
    new TestHelper()
    {
      Resource ecoreResource;
      Resource xmlTypeResource;

      @Override
      void loadResources(ResourceSet resourceSet)
      {
        ecoreResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/Ecore.ecore"), true);
        xmlTypeResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/XMLType.ecore"), true);
      }

      @Override
      void makeChanges()
      {
        EPackage ecorePackage = (EPackage)ecoreResource.getContents().get(0);
        EPackage xmlTypePackage = (EPackage)xmlTypeResource.getContents().get(0);
        xmlTypeResource.getContents().add(ecorePackage.getEClassifiers().get(0));
        ecoreResource.getContents().add(xmlTypePackage.getEClassifiers().get(0));
      }
    }.doit();
  }

  @Test
  public void testSix() throws Exception
  {
    new TestHelper()
    {
      Resource ecoreResource;
      Resource xmlTypeResource;

      @Override
      void loadResources(ResourceSet resourceSet)
      {
        ecoreResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/Ecore.ecore"), true);
        xmlTypeResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/XMLType.ecore"), true);
      }

      @Override
      void makeChanges()
      {
        EPackage ecorePackage = (EPackage)ecoreResource.getContents().get(0);
        EPackage xmlTypePackage = (EPackage)xmlTypeResource.getContents().get(0);
        EClassifier eClassifier = ecorePackage.getEClassifiers().get(10);
        eClassifier.setName(eClassifier.getName() + "Suffix");
        xmlTypePackage.getEClassifiers().get(0).getEAnnotations().get(0).getContents().add(eClassifier);
        eClassifier.setName(eClassifier.getName() + "Suffix");
        xmlTypePackage.getEClassifiers().get(0).setName("BadName");
        xmlTypePackage.getEClassifiers().remove(12);
        xmlTypePackage.getEClassifiers().move(10, 20);
      }
    }.doit();
  }

  @Test
  public void testSeven() throws Exception
  {
    new TestHelper()
    {
      Resource ecoreResource;
      Resource xmlTypeResource;

      @Override
      void loadResources(ResourceSet resourceSet)
      {
        ecoreResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/Ecore.ecore"), true);
        xmlTypeResource = resourceSet.getResource(URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/XMLType.ecore"), true);
      }

      @Override
      void makeChanges()
      {
        EPackage ecorePackage = (EPackage)ecoreResource.getContents().get(0);
        EPackage xmlTypePackage = (EPackage)xmlTypeResource.getContents().get(0);
        EClassifier eClassifier = ecorePackage.getEClassifiers().get(10);
        eClassifier.setName(eClassifier.getName() + "Suffix");
        xmlTypePackage.getEClassifiers().get(0).getEAnnotations().get(0).getContents().add(eClassifier);
        eClassifier.setName(eClassifier.getName() + "Suffix");
        xmlTypePackage.getEClassifiers().get(0).setName("BadName");
        xmlTypePackage.getEClassifiers().remove(12);
        EDataType myEDataType = EcoreFactory.eINSTANCE.createEDataType();
        myEDataType.setName("My");
        xmlTypePackage.getEClassifiers().add(myEDataType);
        myEDataType.setInstanceTypeName("java.util.List<?>");
        xmlTypePackage.getEClassifiers().move(10, 20);
        xmlTypePackage.getEClassifiers().get(12).setName(xmlTypePackage.getEClassifiers().get(10).getName() + "Extension");
        xmlTypePackage.getEClassifiers().remove(12);
      }
    }.doit();
  }

  public static void assertEquals(ResourceSet resourceSet1, ResourceSet resourceSet2)
  {
    EcoreUtil.resolveAll(resourceSet1);
    EcoreUtil.resolveAll(resourceSet2);
    EList<Resource> resources2 = resourceSet2.getResources();
    for (Resource resource2 : resources2)
    {
      assertEquals(resourceSet1.getResource(resource2.getURI(), false), resource2);
    }
  }

  public static void assertEquals(Resource resource1, Resource resource2)
  {
    EList<EObject> eObjects1 = resource1.getContents();
    EList<EObject> eObjects2 = resource2.getContents();
    Assert.assertEquals(eObjects1.size(), eObjects2.size());
    for (int i = 0, size = eObjects1.size(); i < size; ++i)
    {
      assertTrue(EcoreUtil.equals(eObjects1.get(i), eObjects2.get(i)));
    }
  }
}