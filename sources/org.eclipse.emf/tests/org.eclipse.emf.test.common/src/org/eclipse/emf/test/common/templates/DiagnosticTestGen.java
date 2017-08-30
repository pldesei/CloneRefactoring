package org.eclipse.emf.test.common.templates;

import java.util.*;
import org.eclipse.emf.common.util.*;

public class DiagnosticTestGen
{
  protected static String nl;
  public static synchronized DiagnosticTestGen create(String lineSeparator)
  {
    nl = lineSeparator;
    DiagnosticTestGen result = new DiagnosticTestGen();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "import java.util.Iterator;" + NL + "" + NL + "import org.eclipse.emf.common.util.AbstractTreeIterator;" + NL + "import org.eclipse.emf.common.util.Diagnostic;" + NL + "import org.eclipse.emf.common.util.TreeIterator;" + NL + "import org.eclipse.emf.common.util.URI;" + NL + "import org.eclipse.emf.ecore.EPackage;" + NL + "import org.eclipse.emf.ecore.resource.Resource;" + NL + "import org.eclipse.emf.ecore.util.Diagnostician;" + NL + "import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;" + NL + "import org.eclipse.emf.test.common.TestUtil;" + NL + "" + NL + "import static org.junit.Assert.*;" + NL + "import org.junit.Before;" + NL + "import org.junit.Test;" + NL + "" + NL + "public class ";
  protected final String TEXT_2 = "DiagnosticTest" + NL + "{" + NL + "\tprotected Diagnostic diagnostic;" + NL + "\t" + NL + "\t@Before" + NL + "\tpublic void setUp() throws Exception" + NL + "\t{" + NL + "\t\t//TODO:Instantiates the diagnostic here" + NL + "\t\t" + NL + "\t\tassertNotNull(diagnostic);" + NL + "\t}" + NL + "" + NL + "\t@Test" + NL + "\tpublic void testDiagnostic() throws Exception" + NL + "\t{" + NL + "\t\tTreeIterator<Diagnostic> diagnosticIterator = new AbstractTreeIterator<Diagnostic>(diagnostic)" + NL + "\t\t{" + NL + "\t\t\tprivate static final long serialVersionUID = 1L;" + NL + "\t\t\t@Override" + NL + "\t\t\tprotected Iterator<? extends Diagnostic> getChildren(Object object)" + NL + "\t\t\t{" + NL + "\t\t\t\treturn ((Diagnostic)object).getChildren().iterator();" + NL + "\t\t\t}" + NL + "\t\t};" + NL + "" + NL + "\t";
  protected final String TEXT_3 = NL + "\t\tDiagnostic diagnostic";
  protected final String TEXT_4 = " = diagnosticIterator.next();" + NL + "\t\tassertEquals(";
  protected final String TEXT_5 = ", diagnostic";
  protected final String TEXT_6 = ".getSeverity());" + NL + "\t\tassertEquals(";
  protected final String TEXT_7 = ", diagnostic";
  protected final String TEXT_8 = ".getSource());" + NL + "\t\tassertEquals(";
  protected final String TEXT_9 = ", removeObjectHashCode(diagnostic";
  protected final String TEXT_10 = ".getMessage()));" + NL + "\t\tassertEquals(";
  protected final String TEXT_11 = ", diagnostic";
  protected final String TEXT_12 = ".getCode());" + NL + "\t\tassertEquals(";
  protected final String TEXT_13 = ", diagnostic";
  protected final String TEXT_14 = ".getChildren().size());" + NL + "\t\tassertEquals(";
  protected final String TEXT_15 = ", diagnostic";
  protected final String TEXT_16 = ".getData().size());" + NL + "\t\t";
  protected final String TEXT_17 = "assertEquals(";
  protected final String TEXT_18 = ", toString(diagnostic";
  protected final String TEXT_19 = ".getException()));" + NL + "\t\t";
  protected final String TEXT_20 = "assertNull(diagnostic";
  protected final String TEXT_21 = ".getException());";
  protected final String TEXT_22 = NL + "\t\t" + NL + "\t\tassertFalse(diagnosticIterator.hasNext());" + NL + "\t}" + NL + "\t" + NL + "\tprotected String toString(Throwable throwable)" + NL + "\t{" + NL + "\t\tStringBuilder sb = new StringBuilder();" + NL + "\t\tsb.append(throwable.getClass().getName());" + NL + "\t\tsb.append(\"#\").append(throwable.getMessage());" + NL + "" + NL + "\t\tThrowable cause = throwable.getCause();" + NL + "\t\tif (cause != null && cause != throwable)" + NL + "\t\t{" + NL + "\t\t\tsb.append(\"--\").append(toString(cause));" + NL + "\t\t}" + NL + "\t\treturn sb.toString();" + NL + "\t}" + NL + "\t" + NL + "\tprotected String removeObjectHashCode(String string)" + NL + "\t{" + NL + "\t\treturn string.replaceAll(\"@\\\\w+\", \"\");" + NL + "\t}" + NL + "}";
  protected final String TEXT_23 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
/**
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */

    
  String name = (String)((Object[])argument)[0];
  Diagnostic rootDiagnostic = (Diagnostic)((Object[])argument)[1];
  TreeIterator<Diagnostic> diagnosticIterator = new AbstractTreeIterator<Diagnostic>(rootDiagnostic)
  {
    private static final long serialVersionUID = 1L;
    @Override
    protected Iterator<? extends Diagnostic> getChildren(Object object)
    {
      return ((Diagnostic)object).getChildren().iterator();
    }
  };
    
  class Helper
  {
    public String toString(Throwable throwable)
    {
      StringBuilder sb = new StringBuilder();
      sb.append(throwable.getClass().getName());
      sb.append("#").append(throwable.getMessage());

      Throwable cause = throwable.getCause();
      if (cause != null && cause != throwable)
      {
          sb.append("--").append(toString(cause));
      }
      return sb.toString();
    }
    
    protected String removeObjectHashCode(String string)
    {
      return string.replaceAll("@\\w+", "");
    }
    
    
    public String toCodeString(String string)
    {
      return "\"" + string.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"") + "\"";
    }
    
    public String getDiagnosticSeverity(int value)
    {
      switch(value)
      {
        case Diagnostic.CANCEL:
          return "Diagnostic.CANCEL";
        case Diagnostic.OK:
          return "Diagnostic.OK";
        case Diagnostic.INFO:
          return "Diagnostic.INFO";
        case Diagnostic.ERROR:
          return "Diagnostic.ERROR";
        case Diagnostic.WARNING:
          return "Diagnostic.WARNING";
        default:
          return Integer.toString(value);
      }
    }
  }
  Helper helper = new Helper();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_2);
    int count=0; while(diagnosticIterator.hasNext()){Diagnostic diagnostic = diagnosticIterator.next(); count++;
    stringBuffer.append(TEXT_3);
    stringBuffer.append(count);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(helper.getDiagnosticSeverity(diagnostic.getSeverity()));
    stringBuffer.append(TEXT_5);
    stringBuffer.append(count);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(helper.toCodeString(diagnostic.getSource()));
    stringBuffer.append(TEXT_7);
    stringBuffer.append(count);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(helper.removeObjectHashCode(helper.toCodeString(diagnostic.getMessage())));
    stringBuffer.append(TEXT_9);
    stringBuffer.append(count);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(diagnostic.getCode());
    stringBuffer.append(TEXT_11);
    stringBuffer.append(count);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(diagnostic.getChildren().size());
    stringBuffer.append(TEXT_13);
    stringBuffer.append(count);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(diagnostic.getData().size());
    stringBuffer.append(TEXT_15);
    stringBuffer.append(count);
    stringBuffer.append(TEXT_16);
    Throwable throwable = diagnostic.getException(); if (throwable != null) {
    stringBuffer.append(TEXT_17);
    stringBuffer.append(helper.toCodeString(helper.toString(diagnostic.getException())));
    stringBuffer.append(TEXT_18);
    stringBuffer.append(count);
    stringBuffer.append(TEXT_19);
    } else {
    stringBuffer.append(TEXT_20);
    stringBuffer.append(count);
    stringBuffer.append(TEXT_21);
    } if (diagnosticIterator.hasNext()) {stringBuffer.append(NL);}}
    stringBuffer.append(TEXT_22);
    stringBuffer.append(TEXT_23);
    return stringBuffer.toString();
  }
}
