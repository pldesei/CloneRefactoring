/**
 * Copyright (c) 2002-2007 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.test.xml.xmi;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.GenericXMLResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeUtil;
import org.eclipse.emf.test.common.TestUtil;
import org.eclipse.emf.test.models.qname.DocumentRoot;
import org.eclipse.emf.test.models.qname.QNameFactory;
import org.eclipse.emf.test.models.qname.QNamePackage;
import org.eclipse.emf.test.xml.AllSuites;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * XMI tests: loading qname.xml
 */
public class QNameTest
{
  final static String BASE_XML_URI = TestUtil.getPluginDirectory(AllSuites.PLUGIN_ID) + "/data/xml/";

  final static String BASE_XMI_URI = TestUtil.getPluginDirectory(AllSuites.PLUGIN_ID) + "/data/xmi/";

  protected DocumentBuilder builder;

  protected String inputXML;

  protected String expectedXML;

  protected HashMap<String, Object> options;

  @Before
  public void setUp() throws Exception
  {
    QNamePackage.eINSTANCE.getName();
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xml", new XMLResourceFactoryImpl());
    inputXML = BASE_XML_URI + "qname.xml";
    expectedXML = BASE_XMI_URI + "qnameOutput.xml";
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    builder = factory.newDocumentBuilder();
    options = new HashMap<String, Object>();
  }

  @After
  public void tearDown() throws Exception
  {
    builder = null;
    options = null;
  }

  @Test
  public void testQname() throws Exception
  {
    URI uri = URI.createFileURI(inputXML);
    ResourceSet resourceSet = new ResourceSetImpl();
    Resource resource = resourceSet.createResource(uri);
    options.put(XMLResource.OPTION_EXTENDED_META_DATA, ExtendedMetaData.INSTANCE);
    resource.load(options);

    ByteArrayOutputStream outputstream = new ByteArrayOutputStream(2064);
    resource.save(outputstream, options);

    CompareXML.compareFiles(builder, expectedXML, new ByteArrayInputStream(outputstream.toByteArray()));
  }

  @Test
  public void testNullPrefixQname() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml", new GenericXMLResourceFactoryImpl());
    Resource resource = resourceSet.createResource(URI.createURI("dummy.xml"));
    DocumentRoot documentRoot = QNameFactory.eINSTANCE.createDocumentRoot();
    documentRoot.getXMLNSPrefixMap().put("", "namespace");
    documentRoot.setAnyE(XMLTypeFactory.eINSTANCE.createQName("namespace", "qname", ""));
    resource.getContents().add(documentRoot);
    ByteArrayOutputStream outputstream = new ByteArrayOutputStream(2064);
    resource.save(outputstream, options);
    ByteArrayInputStream in = new ByteArrayInputStream(outputstream.toByteArray());
    Resource resource2 = resourceSet.createResource(URI.createURI("dummy2.xml"));
    resource2.load(in, options);
    DocumentRoot documentRoot2 = (DocumentRoot)resource2.getContents().get(0);
    QName qname = documentRoot2.getAnyE();
    assertEquals(qname.getPrefix(), "");
  }

  @Test
  public void testValidQNames()
  {
    QName qname = (QName)XMLTypeUtil.createQName("http://www.example.org/test", "name", "test");
    assertEquals("http://www.example.org/test", qname.getNamespaceURI());
    assertEquals("name", qname.getLocalPart());
    assertEquals("test", qname.getPrefix());

    qname = (QName)XMLTypeUtil.createQName(null, "name", "test");
    assertEquals(XMLConstants.NULL_NS_URI, qname.getNamespaceURI());
    assertEquals("name", qname.getLocalPart());
    assertEquals("test", qname.getPrefix());

    qname = (QName)XMLTypeUtil.createQName("http://www.example.org/test", "name", null);
    assertEquals("http://www.example.org/test", qname.getNamespaceURI());
    assertEquals("name", qname.getLocalPart());
    assertEquals(XMLConstants.DEFAULT_NS_PREFIX, qname.getPrefix());

    qname = (QName)XMLTypeUtil.createQName(null, "name", null);
    assertEquals(XMLConstants.NULL_NS_URI, qname.getNamespaceURI());
    assertEquals("name", qname.getLocalPart());
    assertEquals(XMLConstants.DEFAULT_NS_PREFIX, qname.getPrefix());

    try
    {
      qname = (QName)XMLTypeUtil.createQName("http://www.example.org/test", null, "test");
      fail("null local part is invalid and should not be allowed");
    }
    catch (IllegalArgumentException e)
    {
      // This is expected: a null local part is not allowed.
    }
  }
}