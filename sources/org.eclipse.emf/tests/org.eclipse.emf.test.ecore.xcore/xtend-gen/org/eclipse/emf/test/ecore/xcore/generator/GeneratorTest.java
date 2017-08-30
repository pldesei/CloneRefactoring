/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.test.ecore.xcore.generator;

import com.google.inject.Inject;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xcore.XPackage;
import org.eclipse.emf.ecore.xcore.generator.XcoreGenerator;
import org.eclipse.emf.test.ecore.xcore.XcoreStandaloneInjectorProvider;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(XcoreStandaloneInjectorProvider.class)
@SuppressWarnings("all")
public class GeneratorTest {
  @Inject
  private ParseHelper<XPackage> parser;
  
  @Inject
  private XcoreGenerator xcoreGenerator;
  
  @Test
  public void testGenerator() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("@GenModel(modelDirectory=\"Nowhere\")");
      _builder.newLine();
      _builder.append("package test");
      _builder.newLine();
      _builder.append("class X {}");
      _builder.newLine();
      final XPackage xPackage = this.parser.parse(_builder);
      final InMemoryFileSystemAccess inmemFsa = new InMemoryFileSystemAccess();
      Resource _eResource = xPackage.eResource();
      this.xcoreGenerator.doGenerate(_eResource, inmemFsa);
      Map<String, Object> _allFiles = inmemFsa.getAllFiles();
      Set<String> _keySet = _allFiles.keySet();
      String _string = _keySet.toString();
      Map<String, Object> _allFiles_1 = inmemFsa.getAllFiles();
      int _size = _allFiles_1.size();
      Assert.assertEquals(_string, 8, _size);
      Map<String, Object> _allFiles_2 = inmemFsa.getAllFiles();
      Object _get = _allFiles_2.get((IFileSystemAccess.DEFAULT_OUTPUT + "/test/util/TestSwitch.java"));
      Assert.assertNotNull(_get);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
