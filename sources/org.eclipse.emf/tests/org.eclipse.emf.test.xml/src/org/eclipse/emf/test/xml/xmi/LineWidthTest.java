/**
 * Copyright (c) 2011 Ed Merks and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Ed Merks - Initial API and implementation
 */
package org.eclipse.emf.test.xml.xmi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.GenericXMLResourceFactoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LineWidthTest
{
  protected ResourceSet resourceSet;

  @Before
  public void setUp() throws Exception
  {
    resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml", new GenericXMLResourceFactoryImpl());
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new EcoreResourceFactoryImpl());
  }

  @After
  public void tearDown() throws Exception
  {
    resourceSet = null;
  }

  public void process(String resourceName, int lineWidth, int attributeCount)
  {
    EPackage ecorePackage = EcoreUtil.copy(EcorePackage.eINSTANCE);
    Resource ecoreResource = resourceSet.createResource(URI.createURI(resourceName));
    ecoreResource.getContents().add(ecorePackage);

    Map<Object, Object> options = new HashMap<Object, Object>();
    options.put(XMLResource.OPTION_LINE_WIDTH, lineWidth);

    StringWriter writer1 = new StringWriter();
    StringWriter writer2 = new StringWriter();
    try
    {
      ecoreResource.save(new URIConverter.WriteableOutputStream(writer1, "UTF8"), options);
      options.put(XMLResource.OPTION_USE_FILE_BUFFER, true);
      ecoreResource.save(new URIConverter.WriteableOutputStream(writer2, "UTF8"), options);
    }
    catch (IOException exception)
    {
      fail("Failure saving to XML");
    }

    String string1 = writer1.toString();
    String string2 = writer2.toString();
    assertEquals(string1, string2);
    String[] lines = string1.split("\\n");
    String firstElement = lines[1];
    int count = 0;
    for (int i = 0, length = firstElement.length(); i < length; ++i)
    {
      if (firstElement.charAt(i) == '=')
      {
        ++count;
      }
    }
    assertEquals("First line doesn't contain " + attributeCount + "attribute(s): " + firstElement, attributeCount, count);
  }

  @Test
  public void testXMLShortLine()
  {
    process("Ecore.xml", 10, 0);
  }

  @Test
  public void testXMLMediumLine()
  {
    process("Ecore.xml", 30, 1);
  }

  @Test
  public void testXMLLongLine()
  {
    process("Ecore.xml", 70, 2);
  }

  @Test
  public void testXMLVeryLongLine()
  {
    process("Ecore.xml", 1000, 5);
  }

  @Test
  public void testXMIShortLine()
  {
    process("Ecore.xmi", 10, 0);
  }

  @Test
  public void testXMIMediumLine()
  {
    process("Ecore.xmi", 30, 1);
  }

  @Test
  public void testXMILongLine()
  {
    process("Ecore.xmi", 40, 2);
  }

  @Test
  public void testXMIVeryLongLine()
  {
    process("Ecore.xmi", 1000, 7);
  }
}
