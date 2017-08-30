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
package org.eclipse.emf.test.xml.xsdecore;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.test.common.TestUtil;
import org.eclipse.emf.test.xml.AllSuites;
import org.eclipse.xsd.ecore.EcoreXMLSchemaBuilder;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Test for conversion from XSD to ECore - compare against expected output
 */
public class Ecore2XSDTest
{

  /** Schema full checking feature id (http://apache.org/xml/features/validation/schema-full-checking). */
  protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";

  // base uri of the xsd and ecore files
  final static String BASE_ECORE_URI = "file:///" + TestUtil.getPluginDirectory(AllSuites.PLUGIN_ID) + "/data/ecore/";


  /** ecore files */
  protected String multipleInheritanceA = null;

  protected String multipleInheritanceB = null;

  protected String multipleInheritanceB1 = null;

  protected SAXParser parser;

  protected EcoreXMLSchemaBuilder ecoreXSDBuilder;

  protected Resource resource;

  protected ByteArrayInputStream dummyXML;

  protected MyHandler handler = new MyHandler();

  @Before
  public void setUp() throws Exception
  {
    ecoreXSDBuilder = new EcoreXMLSchemaBuilder();

    dummyXML = new ByteArrayInputStream("<root/>".getBytes());

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());

    SAXParserFactory fac = SAXParserFactory.newInstance();
    fac.setNamespaceAware(true);
    fac.setValidating(true);
    parser = fac.newSAXParser();
    try
    {
      parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
    }
    catch (SAXNotRecognizedException e)
    {
      assertNull("WARNING! Test was not run: "+e.getMessage()+" is not supported", e);
    }
    catch (SAXNotSupportedException e)
    {
      assertNull("WARNING! Test was not run: "+e.getMessage()+" is not supported", e);
    }

    // Add in the right order the files to compare with the output
    multipleInheritanceA = BASE_ECORE_URI + "multipleInheritanceA.ecore";
    multipleInheritanceB = BASE_ECORE_URI + "multipleInheritanceB.ecore";
    multipleInheritanceB1 = BASE_ECORE_URI + "multipleInheritanceB1.ecore";
  }

  @Test
  public void testMultipleInheritanceA() throws Exception
  {
    ecore2xsd(multipleInheritanceA);
  }

  @Test
  public void testMultipleInheritanceB() throws Exception
  {
    ecore2xsd(multipleInheritanceB);
  }

  @Test
  public void testMultipleInheritanceB1() throws Exception
  {
    ecore2xsd(multipleInheritanceB1);
  }

  public void ecore2xsd(String fileName) throws Exception
  {

    URI uri = URI.createURI(fileName);
    ResourceSet rs = new ResourceSetImpl();
    Resource ecore = rs.createResource(uri);
    ecore.load(null);

    Collection<EObject> c = ecoreXSDBuilder.generate((EPackage)ecore.getContents().get(0));

    Resource schema = rs.createResource(URI.createURI("my.xsd"));
    schema.getContents().add(c.iterator().next());
    ByteArrayOutputStream outputstream = new ByteArrayOutputStream(2064);
    schema.save(outputstream, null);

    parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", new ByteArrayInputStream(outputstream.toByteArray()));
    parser.parse(dummyXML, handler);
    assertEquals(handler.getErrorMessage(), 0, handler.getErrors());
  }

  class MyHandler extends DefaultHandler
  {
    int errors;

    ArrayList<String> list = new ArrayList<String>();

    @Override
    public void startDocument() throws SAXException
    {
      errors = 0;
      list.clear();
    }

    @Override
    public void error(SAXParseException arg0) throws SAXException
    {
      errors++;
      list.add(arg0.toString());
    }

    @Override
    public void fatalError(SAXParseException arg0) throws SAXException
    {
      assertEquals(arg0.toString(), 1, 0);
    }

    public int getErrors()
    {
      // one error message does not count since we are reporting dummy
      // error for root declaration not found
      return errors - 1;
    }

    public String getErrorMessage()
    {
      list.remove(list.size() - 1);
      return list.toString();
    }
  }
}