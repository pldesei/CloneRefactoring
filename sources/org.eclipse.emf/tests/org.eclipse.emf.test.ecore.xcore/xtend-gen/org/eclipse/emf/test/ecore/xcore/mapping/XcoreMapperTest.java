/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.test.ecore.xcore.mapping;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.codegen.ecore.genmodel.GenOperation;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenParameter;
import org.eclipse.emf.codegen.ecore.genmodel.GenTypeParameter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.xcore.XClass;
import org.eclipse.emf.ecore.xcore.XClassifier;
import org.eclipse.emf.ecore.xcore.XMember;
import org.eclipse.emf.ecore.xcore.XNamedElement;
import org.eclipse.emf.ecore.xcore.XOperation;
import org.eclipse.emf.ecore.xcore.XPackage;
import org.eclipse.emf.ecore.xcore.XParameter;
import org.eclipse.emf.ecore.xcore.XStructuralFeature;
import org.eclipse.emf.ecore.xcore.XTypeParameter;
import org.eclipse.emf.ecore.xcore.mappings.ToXcoreMapping;
import org.eclipse.emf.ecore.xcore.mappings.XClassMapping;
import org.eclipse.emf.ecore.xcore.mappings.XFeatureMapping;
import org.eclipse.emf.ecore.xcore.mappings.XOperationMapping;
import org.eclipse.emf.ecore.xcore.mappings.XPackageMapping;
import org.eclipse.emf.ecore.xcore.mappings.XParameterMapping;
import org.eclipse.emf.ecore.xcore.mappings.XTypeParameterMapping;
import org.eclipse.emf.ecore.xcore.mappings.XcoreMapper;
import org.eclipse.emf.test.ecore.xcore.XcoreStandaloneInjectorProvider;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(XcoreStandaloneInjectorProvider.class)
@SuppressWarnings("all")
public class XcoreMapperTest {
  @Inject
  private ParseHelper<XPackage> parser;
  
  @Inject
  @Extension
  private XcoreMapper mapper;
  
  @Test
  public void testMapping() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("package foo.bar");
      _builder.newLine();
      _builder.newLine();
      _builder.append("type String wraps java.lang.String");
      _builder.newLine();
      _builder.newLine();
      _builder.append("class X {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("attr String name");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("refers Y reference");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("class Y<T> extends X {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("op String toString(X x) {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("return null");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final XPackage pack = this.parser.parse(_builder);
      XPackageMapping _mapping = this.mapper.getMapping(pack);
      EPackage _ePackage = _mapping.getEPackage();
      Assert.assertNotNull(_ePackage);
      XPackageMapping _mapping_1 = this.mapper.getMapping(pack);
      EPackage _ePackage_1 = _mapping_1.getEPackage();
      XPackageMapping _mapping_2 = this.mapper.getMapping(pack);
      GenPackage _genPackage = _mapping_2.getGenPackage();
      EPackage _ecorePackage = _genPackage.getEcorePackage();
      Assert.assertEquals(_ePackage_1, _ecorePackage);
      XPackageMapping _mapping_3 = this.mapper.getMapping(pack);
      GenPackage _genPackage_1 = _mapping_3.getGenPackage();
      ToXcoreMapping _toXcoreMapping = this.mapper.getToXcoreMapping(_genPackage_1);
      XNamedElement _xcoreElement = _toXcoreMapping.getXcoreElement();
      Assert.assertEquals(pack, _xcoreElement);
      XPackageMapping _mapping_4 = this.mapper.getMapping(pack);
      EPackage _ePackage_2 = _mapping_4.getEPackage();
      ToXcoreMapping _toXcoreMapping_1 = this.mapper.getToXcoreMapping(_ePackage_2);
      XNamedElement _xcoreElement_1 = _toXcoreMapping_1.getXcoreElement();
      Assert.assertEquals(pack, _xcoreElement_1);
      EList<XClassifier> _classifiers = pack.getClassifiers();
      Iterable<XClass> _filter = Iterables.<XClass>filter(_classifiers, XClass.class);
      for (final XClass clazz : _filter) {
        {
          XClassMapping _mapping_5 = this.mapper.getMapping(clazz);
          EClass _eClass = _mapping_5.getEClass();
          Assert.assertNotNull(_eClass);
          XClassMapping _mapping_6 = this.mapper.getMapping(clazz);
          EClass _eClass_1 = _mapping_6.getEClass();
          XClassMapping _mapping_7 = this.mapper.getMapping(clazz);
          GenClass _genClass = _mapping_7.getGenClass();
          EClass _ecoreClass = _genClass.getEcoreClass();
          Assert.assertEquals(_eClass_1, _ecoreClass);
          XClassMapping _mapping_8 = this.mapper.getMapping(clazz);
          GenClass _genClass_1 = _mapping_8.getGenClass();
          ToXcoreMapping _toXcoreMapping_2 = this.mapper.getToXcoreMapping(_genClass_1);
          XNamedElement _xcoreElement_2 = _toXcoreMapping_2.getXcoreElement();
          Assert.assertEquals(clazz, _xcoreElement_2);
          XClassMapping _mapping_9 = this.mapper.getMapping(clazz);
          EClass _eClass_2 = _mapping_9.getEClass();
          ToXcoreMapping _toXcoreMapping_3 = this.mapper.getToXcoreMapping(_eClass_2);
          XNamedElement _xcoreElement_3 = _toXcoreMapping_3.getXcoreElement();
          Assert.assertEquals(clazz, _xcoreElement_3);
          EList<XMember> _members = clazz.getMembers();
          for (final XMember member : _members) {
            boolean _matched = false;
            if (member instanceof XStructuralFeature) {
              _matched=true;
              XFeatureMapping _mapping_10 = this.mapper.getMapping(((XStructuralFeature)member));
              EStructuralFeature _eStructuralFeature = _mapping_10.getEStructuralFeature();
              Assert.assertNotNull(_eStructuralFeature);
              XFeatureMapping _mapping_11 = this.mapper.getMapping(((XStructuralFeature)member));
              EStructuralFeature _eStructuralFeature_1 = _mapping_11.getEStructuralFeature();
              XFeatureMapping _mapping_12 = this.mapper.getMapping(((XStructuralFeature)member));
              GenFeature _genFeature = _mapping_12.getGenFeature();
              EStructuralFeature _ecoreFeature = _genFeature.getEcoreFeature();
              Assert.assertEquals(_eStructuralFeature_1, _ecoreFeature);
              XFeatureMapping _mapping_13 = this.mapper.getMapping(((XStructuralFeature)member));
              EStructuralFeature _eStructuralFeature_2 = _mapping_13.getEStructuralFeature();
              ToXcoreMapping _toXcoreMapping_4 = this.mapper.getToXcoreMapping(_eStructuralFeature_2);
              XNamedElement _xcoreElement_4 = _toXcoreMapping_4.getXcoreElement();
              Assert.assertEquals(member, _xcoreElement_4);
              XFeatureMapping _mapping_14 = this.mapper.getMapping(((XStructuralFeature)member));
              GenFeature _genFeature_1 = _mapping_14.getGenFeature();
              ToXcoreMapping _toXcoreMapping_5 = this.mapper.getToXcoreMapping(_genFeature_1);
              XNamedElement _xcoreElement_5 = _toXcoreMapping_5.getXcoreElement();
              Assert.assertEquals(member, _xcoreElement_5);
            }
            if (!_matched) {
              if (member instanceof XOperation) {
                _matched=true;
                XOperationMapping _mapping_10 = this.mapper.getMapping(((XOperation)member));
                EOperation _eOperation = _mapping_10.getEOperation();
                Assert.assertNotNull(_eOperation);
                XOperationMapping _mapping_11 = this.mapper.getMapping(((XOperation)member));
                EOperation _eOperation_1 = _mapping_11.getEOperation();
                XOperationMapping _mapping_12 = this.mapper.getMapping(((XOperation)member));
                GenOperation _genOperation = _mapping_12.getGenOperation();
                EOperation _ecoreOperation = _genOperation.getEcoreOperation();
                Assert.assertEquals(_eOperation_1, _ecoreOperation);
                XOperationMapping _mapping_13 = this.mapper.getMapping(((XOperation)member));
                EOperation _eOperation_2 = _mapping_13.getEOperation();
                ToXcoreMapping _toXcoreMapping_4 = this.mapper.getToXcoreMapping(_eOperation_2);
                XNamedElement _xcoreElement_4 = _toXcoreMapping_4.getXcoreElement();
                Assert.assertEquals(member, _xcoreElement_4);
                XOperationMapping _mapping_14 = this.mapper.getMapping(((XOperation)member));
                GenOperation _genOperation_1 = _mapping_14.getGenOperation();
                ToXcoreMapping _toXcoreMapping_5 = this.mapper.getToXcoreMapping(_genOperation_1);
                XNamedElement _xcoreElement_5 = _toXcoreMapping_5.getXcoreElement();
                Assert.assertEquals(member, _xcoreElement_5);
                EList<XParameter> _parameters = ((XOperation)member).getParameters();
                for (final XParameter parameter : _parameters) {
                  {
                    XParameterMapping _mapping_15 = this.mapper.getMapping(parameter);
                    EParameter _eParameter = _mapping_15.getEParameter();
                    Assert.assertNotNull(_eParameter);
                    XParameterMapping _mapping_16 = this.mapper.getMapping(parameter);
                    EParameter _eParameter_1 = _mapping_16.getEParameter();
                    XParameterMapping _mapping_17 = this.mapper.getMapping(parameter);
                    GenParameter _genParameter = _mapping_17.getGenParameter();
                    EParameter _ecoreParameter = _genParameter.getEcoreParameter();
                    Assert.assertEquals(_eParameter_1, _ecoreParameter);
                    XParameterMapping _mapping_18 = this.mapper.getMapping(parameter);
                    EParameter _eParameter_2 = _mapping_18.getEParameter();
                    ToXcoreMapping _toXcoreMapping_6 = this.mapper.getToXcoreMapping(_eParameter_2);
                    XNamedElement _xcoreElement_6 = _toXcoreMapping_6.getXcoreElement();
                    Assert.assertEquals(parameter, _xcoreElement_6);
                    XParameterMapping _mapping_19 = this.mapper.getMapping(parameter);
                    GenParameter _genParameter_1 = _mapping_19.getGenParameter();
                    ToXcoreMapping _toXcoreMapping_7 = this.mapper.getToXcoreMapping(_genParameter_1);
                    XNamedElement _xcoreElement_7 = _toXcoreMapping_7.getXcoreElement();
                    Assert.assertEquals(parameter, _xcoreElement_7);
                  }
                }
              }
            }
          }
          EList<XTypeParameter> _typeParameters = clazz.getTypeParameters();
          for (final XTypeParameter typeParameter : _typeParameters) {
            {
              XTypeParameterMapping _mapping_10 = this.mapper.getMapping(typeParameter);
              ETypeParameter _eTypeParameter = _mapping_10.getETypeParameter();
              Assert.assertNotNull(_eTypeParameter);
              XTypeParameterMapping _mapping_11 = this.mapper.getMapping(typeParameter);
              ETypeParameter _eTypeParameter_1 = _mapping_11.getETypeParameter();
              XTypeParameterMapping _mapping_12 = this.mapper.getMapping(typeParameter);
              GenTypeParameter _genTypeParameter = _mapping_12.getGenTypeParameter();
              ETypeParameter _ecoreTypeParameter = _genTypeParameter.getEcoreTypeParameter();
              Assert.assertEquals(_eTypeParameter_1, _ecoreTypeParameter);
              XTypeParameterMapping _mapping_13 = this.mapper.getMapping(typeParameter);
              ETypeParameter _eTypeParameter_2 = _mapping_13.getETypeParameter();
              ToXcoreMapping _toXcoreMapping_4 = this.mapper.getToXcoreMapping(_eTypeParameter_2);
              XNamedElement _xcoreElement_4 = _toXcoreMapping_4.getXcoreElement();
              Assert.assertEquals(typeParameter, _xcoreElement_4);
              XTypeParameterMapping _mapping_14 = this.mapper.getMapping(typeParameter);
              GenTypeParameter _genTypeParameter_1 = _mapping_14.getGenTypeParameter();
              ToXcoreMapping _toXcoreMapping_5 = this.mapper.getToXcoreMapping(_genTypeParameter_1);
              XNamedElement _xcoreElement_5 = _toXcoreMapping_5.getXcoreElement();
              Assert.assertEquals(typeParameter, _xcoreElement_5);
            }
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
