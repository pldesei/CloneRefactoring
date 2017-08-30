/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.test.ecore.xcore.generator

import com.google.inject.Inject
import org.eclipse.emf.ecore.xcore.XPackage
import org.eclipse.emf.ecore.xcore.generator.XcoreGenerator
import org.eclipse.xtext.generator.InMemoryFileSystemAccess
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.emf.test.ecore.xcore.XcoreStandaloneInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(XcoreStandaloneInjectorProvider))
class GeneratorTest {
	
	@Inject
	ParseHelper<XPackage> parser
	
	@Inject
	XcoreGenerator xcoreGenerator
	
	@Test
	def void testGenerator() {
		val xPackage = parser.parse('''
			@GenModel(modelDirectory="Nowhere")
			package test
			class X {}
		''')
		val inmemFsa = new InMemoryFileSystemAccess()
		xcoreGenerator.doGenerate(xPackage.eResource, inmemFsa)
		assertEquals(inmemFsa.allFiles.keySet.toString, 8, inmemFsa.allFiles.size)
		
		assertNotNull(inmemFsa.allFiles.get(IFileSystemAccess::DEFAULT_OUTPUT+'/test/util/TestSwitch.java'))
	}
}