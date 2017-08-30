/*******************************************************************************
 * Copyright (c) 2006, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jesper Steen Moeller - Contributions for:
 *         Bug 407297: [1.8][compiler] Control generation of parameter names by option
 *******************************************************************************/
package org.eclipse.jdt.compiler.tool.tests;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import junit.framework.TestCase;

import org.eclipse.jdt.compiler.tool.tests.AbstractCompilerToolTest.CompilerInvocationDiagnosticListener;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

public class CompilerToolTests extends TestCase {
	private static final boolean DEBUG = false;

	public CompilerToolTests(String name) {
		super(name);
	}

	private JavaCompiler compiler;
	static final String[] ONE_ARG_OPTIONS = {
		"-cp",
		"-classpath",
		"-bootclasspath",
		"-sourcepath",
		"-extdirs",
		"-endorseddirs",
		"-d",
		"-encoding",
		"-source",
		"-target",
		"-maxProblems",
		"-log",
		"-repeat",
		"-processorpath",
		"-s",
		"-processor",
		"-classNames"
	};
	static final String[] ZERO_ARG_OPTIONS = {
		"-1.3",
		"-1.4",
		"-1.5",
		"-1.6",
		"-1.7",
		"-1.8",
		"-8",
		"-8.0",
		"-7",
		"-7.0",
		"-6",
		"-6.0",
		"-5",
		"-5.0",
		"-deprecation",
		"-nowarn",
		"-warn:none",
		"-?:warn",
		"-g",
		"-g:lines",
		"-g:source",
		"-g:vars",
		"-g:lines,vars",
		"-g:none",
		"-preserveAllLocals",
		"-X",
		"-O",
		"-proceedOnError",
		"-proceedOnError:Fatal",
		"-verbose",
		"-referenceInfo",
		"-progress",
		"-time",
		"-noExit",
		"-inlineJSR",
		"-enableJavadoc",
		"-Xemacs",
		"-?",
		"-help",
		"-v",
		"-version",
		"-showversion",
		"-XprintRounds",
		"-XprintProcessorInfo",
		"-proc:none",
		"-proc:only",
		"-parameters",
		"-genericsignature"
	};
static final String[] FAKE_ZERO_ARG_OPTIONS = new String[] { 
	// a series of fake options to test the behavior upon ignored and 
	// pass-through options
	"-Jignore",
	"-Xignore",
	"-Akey=value",
};

	private void displayLocation(StandardJavaFileManager manager, StandardLocation standardLocation) {
		System.out.println(standardLocation.getName());
		Iterable<? extends File> location = manager.getLocation(standardLocation);
		if (location == null) return;
		for (File f : location) {
			System.out.println(f.getAbsolutePath());
		}
	}

	@Override
	protected void setUp() throws Exception {
		ServiceLoader<JavaCompiler> javaCompilerLoader = ServiceLoader.load(JavaCompiler.class);//, EclipseCompiler.class.getClassLoader());
		int compilerCounter = 0;
		for (JavaCompiler javaCompiler : javaCompilerLoader) {
			compilerCounter++;
			if (javaCompiler instanceof EclipseCompiler) {
				compiler = javaCompiler;
			}
		}
		assertEquals("Only one compiler available", 1, compilerCounter);
	}

	@Override
	protected void tearDown() throws Exception {
		compiler = null;
	}

	public void testCheckOptions() {
		assertNotNull("No compiler found", compiler);
		for (String option : ONE_ARG_OPTIONS) {
			assertEquals(option + " requires 1 argument", 1, compiler.isSupportedOption(option));
		}
		for (String option : ZERO_ARG_OPTIONS) {
			assertEquals(option + " requires no argument", 0, compiler.isSupportedOption(option));
		}
		for (String option : FAKE_ZERO_ARG_OPTIONS) {
			assertEquals(option + " requires no argument", 0, compiler.isSupportedOption(option));
		}
	}

	public void testCompilerOneClassWithSystemCompiler() {
		JavaCompiler systemCompiler = ToolProvider.getSystemJavaCompiler();
		if (systemCompiler == null) {
			System.out.println("No system java compiler available");
			return;
		}
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File inputFile = new File(tmpFolder, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(
				"package p;\n" +
				"public class X {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		// System compiler
		StandardJavaFileManager manager = systemCompiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());

		ForwardingJavaFileManager<JavaFileManager> forwardingJavaFileManager = new ForwardingJavaFileManager<JavaFileManager>(manager) {
			@Override
			public FileObject getFileForInput(Location location, String packageName, String relativeName)
					throws IOException {
				if (DEBUG) {
					System.out.println("Create file for input : " + packageName + " " + relativeName + " in location " + location);
				}
				return super.getFileForInput(location, packageName, relativeName);
			}
			@Override
			public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind)
					throws IOException {
				if (DEBUG) {
					System.out.println("Create java file for input : " + className + " in location " + location);
				}
				return super.getJavaFileForInput(location, className, kind);
			}
			@Override
			public JavaFileObject getJavaFileForOutput(Location location,
					String className,
					Kind kind,
					FileObject sibling) throws IOException {

				if (DEBUG) {
					System.out.println("Create .class file for " + className + " in location " + location + " with sibling " + sibling.toUri());
				}
				JavaFileObject javaFileForOutput = super.getJavaFileForOutput(location, className, kind, sibling);
				if (DEBUG) {
					System.out.println(javaFileForOutput.toUri());
				}
				return javaFileForOutput;
			}
		};
		// create new list containing inputfile
		List<File> files = new ArrayList<File>();
		files.add(inputFile);
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(tmpFolder);
 		CompilationTask task = systemCompiler.getTask(printWriter, forwardingJavaFileManager, null, options, null, units);
 		assertTrue("Has location CLASS_OUPUT ", forwardingJavaFileManager.hasLocation(StandardLocation.CLASS_OUTPUT));
		Boolean result = task.call();

 		if (!result.booleanValue()) {
 			System.err.println("Compilation failed: " + stringWriter.getBuffer().toString());
 	 		assertTrue("Compilation failed ", false);
 		}
		// check that the .class file exist for X
		assertTrue("delete failed", inputFile.delete());
	}

	/*
	 * Run the system compiler using the Eclipse java file manager
	 * TODO need to investigate why rt.jar gets removed from the PLATFORM_CLASSPATH location
	 */
	public void testCompilerOneClassWithSystemCompiler2() {
		// System compiler
		JavaCompiler systemCompiler = ToolProvider.getSystemJavaCompiler();
		if (systemCompiler == null) {
			System.out.println("No system java compiler available");
			return;
		}
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File inputFile = new File(tmpFolder, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(
				"package p;\n" +
				"public class X {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());

		ForwardingJavaFileManager<JavaFileManager> forwardingJavaFileManager = new ForwardingJavaFileManager<JavaFileManager>(manager) {
			@Override
			public FileObject getFileForInput(Location location, String packageName, String relativeName)
					throws IOException {
				if (DEBUG) {
					System.out.println("Create file for input : " + packageName + " " + relativeName + " in location " + location);
				}
				return super.getFileForInput(location, packageName, relativeName);
			}
			@Override
			public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind)
					throws IOException {
				if (DEBUG) {
					System.out.println("Create java file for input : " + className + " in location " + location);
				}
				return super.getJavaFileForInput(location, className, kind);
			}
			@Override
			public JavaFileObject getJavaFileForOutput(Location location,
					String className,
					Kind kind,
					FileObject sibling) throws IOException {

				if (DEBUG) {
					System.out.println("Create .class file for " + className + " in location " + location + " with sibling " + sibling.toUri());
				}
				JavaFileObject javaFileForOutput = super.getJavaFileForOutput(location, className, kind, sibling);
				if (DEBUG) {
					System.out.println(javaFileForOutput.toUri());
				}
				return javaFileForOutput;
			}
			@Override
			public String inferBinaryName(Location location, JavaFileObject file) {
				String binaryName = super.inferBinaryName(location, file);
				if (DEBUG) {
					System.out.println("binary name: " + binaryName);
				}
				return binaryName;
			}
			@Override
			public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
					throws IOException {
				Iterable<JavaFileObject> list = super.list(location, packageName, kinds, recurse);
				if (DEBUG) {
					System.out.println("start list: ");
					for (JavaFileObject fileObject : list) {
						System.out.println(fileObject.getName());
					}
					System.out.println("end   list: ");
				}
				return list;
			}
		};
		// create new list containing input file
		List<File> files = new ArrayList<File>();
		files.add(inputFile);
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(tmpFolder);
		CompilationTask task = systemCompiler.getTask(printWriter, forwardingJavaFileManager, null, options, null, units);

		if (DEBUG) {
			System.out.println("Has location CLASS_OUPUT : " + forwardingJavaFileManager.hasLocation(StandardLocation.CLASS_OUTPUT));
		}

		Boolean result = task.call();
		displayLocation(manager, StandardLocation.CLASS_PATH);
		displayLocation(manager, StandardLocation.PLATFORM_CLASS_PATH);

		if (!result.booleanValue()) {
			System.err.println("Compilation failed: " + stringWriter.getBuffer().toString());
			assertTrue("Compilation failed ", false);
		}
		// check that the .class file exist for X
		assertTrue("delete failed", inputFile.delete());
	}

	public void testCompilerOneClassWithEclipseCompiler() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File inputFile = new File(tmpFolder, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(
				"package p;\n" +
				"public class X {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		// System compiler
		StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());

		ForwardingJavaFileManager<StandardJavaFileManager> forwardingJavaFileManager = new ForwardingJavaFileManager<StandardJavaFileManager>(manager) {
			@Override
			public FileObject getFileForInput(Location location, String packageName, String relativeName)
					throws IOException {
				if (DEBUG) {
					System.out.println("Create file for input : " + packageName + " " + relativeName + " in location " + location);
				}
				return super.getFileForInput(location, packageName, relativeName);
			}
			@Override
			public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind)
					throws IOException {
				if (DEBUG) {
					System.out.println("Create java file for input : " + className + " in location " + location);
				}
				return super.getJavaFileForInput(location, className, kind);
			}
			@Override
			public JavaFileObject getJavaFileForOutput(Location location,
					String className,
					Kind kind,
					FileObject sibling) throws IOException {

				if (DEBUG) {
					System.out.println("Create .class file for " + className + " in location " + location + " with sibling " + sibling.toUri());
				}
				JavaFileObject javaFileForOutput = super.getJavaFileForOutput(location, className, kind, sibling);
				if (DEBUG) {
					System.out.println(javaFileForOutput.toUri());
				}
				return javaFileForOutput;
			}
		};
		// create new list containing input file
		List<File> files = new ArrayList<File>();
		files.add(inputFile);
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(tmpFolder);
 		CompilationTask task = compiler.getTask(printWriter, forwardingJavaFileManager, null, options, null, units);
 		// check the classpath location
 		assertTrue("Has no location CLASS_OUPUT", forwardingJavaFileManager.hasLocation(StandardLocation.CLASS_OUTPUT));
		Boolean result = task.call();
		printWriter.flush();
		printWriter.close();
 		if (!result.booleanValue()) {
 			System.err.println("Compilation failed: " + stringWriter.getBuffer().toString());
 	 		assertTrue("Compilation failed ", false);
 		}
 		ClassFileReader reader = null;
 		try {
			reader = ClassFileReader.read(new File(tmpFolder, "p/X.class"), true);
		} catch (ClassFormatException e) {
			assertTrue("Should not happen", false);
		} catch (IOException e) {
			assertTrue("Should not happen", false);
		}
		assertNotNull("No reader", reader);
 		assertEquals("Wrong value", ClassFileConstants.JDK1_6, reader.getVersion());
		// check that the .class file exist for X
		assertTrue("delete failed", inputFile.delete());
	}

	public void testCompilerOneClassWithEclipseCompiler2() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File inputFile = new File(tmpFolder, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(
				"package p;\n" +
				"public class X {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		// System compiler
		StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());

		ForwardingJavaFileManager<StandardJavaFileManager> forwardingJavaFileManager = new ForwardingJavaFileManager<StandardJavaFileManager>(manager) {
			@Override
			public JavaFileObject getJavaFileForOutput(Location location,
					String className,
					Kind kind,
					FileObject sibling) throws IOException {

				if (DEBUG) {
					System.out.println("EC: Create .class file for " + className + " in location " + location + " with sibling " + sibling.toUri());
				}
				JavaFileObject javaFileForOutput = super.getJavaFileForOutput(location, className, kind, sibling);
				if (DEBUG) {
					System.out.println(javaFileForOutput.toUri());
				}
				return javaFileForOutput;
			}
		};
		// create new list containing inputfile
		List<File> files = new ArrayList<File>();
		files.add(inputFile);
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(tmpFolder);
		options.add("-1.5");
 		CompilationTask task = compiler.getTask(printWriter, forwardingJavaFileManager, null, options, null, units);
 		// check the classpath location
 		assertTrue("Has no location CLASS_OUPUT", forwardingJavaFileManager.hasLocation(StandardLocation.CLASS_OUTPUT));
		Boolean result = task.call();
		printWriter.flush();
		printWriter.close();
		if (!result.booleanValue()) {
			System.err.println("Compilation failed: " + stringWriter.getBuffer().toString());
			assertTrue("Compilation failed ", false);
		}
		File outputFile = new File(tmpFolder, "p/X.class");
		assertTrue(outputFile.exists());
		ClassFileReader reader = null;
		try {
			reader = ClassFileReader.read(outputFile);
		} catch (ClassFormatException e) {
			assertTrue("Should not happen", false);
		} catch (IOException e) {
			assertTrue("Should not happen", false);
		}
		assertNotNull("No reader", reader);
		assertEquals("Not a 1.5 .class file", ClassFileConstants.JDK1_5, reader.getVersion());

		stringWriter = new StringWriter();
		printWriter = new PrintWriter(stringWriter);
		task = compiler.getTask(printWriter, forwardingJavaFileManager, null, options, null, units);
		// check the classpath location
		assertTrue("Has no location CLASS_OUPUT", forwardingJavaFileManager.hasLocation(StandardLocation.CLASS_OUTPUT));
		result = task.call();
		printWriter.flush();
		printWriter.close();
		if (!result.booleanValue()) {
		System.err.println("Compilation failed: " + stringWriter.getBuffer().toString());
			assertTrue("Compilation failed ", false);
		}
		// check that the .class file exist for X
		assertTrue("delete failed", inputFile.delete());
	}

	public void testCompilerOneClassWithEclipseCompiler3() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File inputFile = new File(tmpFolder, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(
				"package p;\n" +
				"public class X {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		// System compiler
		StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());

		@SuppressWarnings("resource")
		ForwardingJavaFileManager<StandardJavaFileManager> forwardingJavaFileManager = new ForwardingJavaFileManager<StandardJavaFileManager>(manager) {
			@Override
			public JavaFileObject getJavaFileForOutput(Location location,
					String className,
					Kind kind,
					FileObject sibling) throws IOException {

				if (DEBUG) {
					System.out.println("Create .class file for " + className + " in location " + location + " with sibling " + sibling.toUri());
				}
				JavaFileObject javaFileForOutput = super.getJavaFileForOutput(location, className, kind, sibling);
				if (DEBUG) {
					System.out.println(javaFileForOutput.toUri());
				}
				return javaFileForOutput;
			}
		};
		// create new list containing inputfile
		List<File> files = new ArrayList<File>();
		files.add(inputFile);
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(tmpFolder);
 		CompilationTask task = compiler.getTask(printWriter, manager, null, options, null, units);
 		// check the classpath location
 		assertTrue("Has no location CLASS_OUPUT", forwardingJavaFileManager.hasLocation(StandardLocation.CLASS_OUTPUT));
		Boolean result = task.call();
		printWriter.flush();
		printWriter.close();
 		if (!result.booleanValue()) {
 			System.err.println("Compilation failed: " + stringWriter.getBuffer().toString());
 	 		assertTrue("Compilation failed ", false);
 		}

		try {
			task.call();
			assertTrue("Should not get there", false);
		} catch (IllegalStateException e) {
			// ignore: expected
		}

		// check that the .class file exist for X
		assertTrue("delete failed", inputFile.delete());
	}

	public void testCompilerOneClassWithEclipseCompiler4() throws IOException {
		JavaCompiler systemCompiler = ToolProvider.getSystemJavaCompiler();
		if (systemCompiler == null) {
			System.out.println("No system java compiler available");
			return;
		}
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File inputFile = new File(tmpFolder, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(
				"package p;\n" +
				"public class X {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		// create new list containing inputfile
		List<File> files = new ArrayList<File>();
		files.add(inputFile);
		StandardJavaFileManager manager = systemCompiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(tmpFolder);
		CompilationTask task = compiler.getTask(null, null, null, options, null, units);
		// check the classpath location
		Boolean result = task.call();
		printWriter.flush();
		printWriter.close();
		if (!result.booleanValue()) {
			System.err.println("Compilation failed: " + stringWriter.getBuffer().toString());
			assertTrue("Compilation failed ", false);
		}
		// check that the .class file exist for X
		assertTrue("delete failed", inputFile.delete());
		manager.close();
	}

	public void testCompilerOneClassWithEclipseCompiler5() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File inputFile = new File(tmpFolder, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(
				"package p;\n" +
				"public class X extends File {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		// System compiler
		StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
		List<File> files = new ArrayList<File>();
		files.add(inputFile);
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);

		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(tmpFolder);
		ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
		PrintWriter err = new PrintWriter(errBuffer);
		CompilerInvocationDiagnosticListener compilerInvocationDiagnosticListener = new CompilerInvocationDiagnosticListener(err);
		CompilationTask task = compiler.getTask(null, manager, compilerInvocationDiagnosticListener, options, null, units);
		// check the classpath location
		Boolean result = task.call();
		err.flush();
		err.close();
		assertFalse(errBuffer.toString().isEmpty());
		assertTrue(compilerInvocationDiagnosticListener.kind != CompilerInvocationDiagnosticListener.NONE);
		if (!result.booleanValue()) {
			assertFalse("Compilation did not fail", false);
		}
		// check that the .class file exist for X
		assertTrue("delete failed", inputFile.delete());
	}

	public void testCompilerOneClassWithEclipseCompiler6() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File packageFolder = new File(tmpFolder, "p");
		if (!packageFolder.mkdirs()) {
			return;
		}
		File inputFile = new File(packageFolder, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(
				"package p;\n" +
				"public class X extends File {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		// System compiler
		StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
		List<File> files = new ArrayList<File>();
		files.add(inputFile);
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
	
		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(tmpFolder);
		options.add("-sourcepath");
		options.add(tmpFolder);
		ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
		PrintWriter err = new PrintWriter(errBuffer);
		CompilerInvocationDiagnosticListener compilerInvocationDiagnosticListener = new CompilerInvocationDiagnosticListener(err) {
			@Override
			public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
				JavaFileObject source = diagnostic.getSource();
				assertNotNull("No source", source);
				super.report(diagnostic);
			}
		};
		CompilationTask task = compiler.getTask(null, manager, compilerInvocationDiagnosticListener, options, null, units);
		// check the classpath location
		Boolean result = task.call();
		err.flush();
		err.close();
		assertFalse(errBuffer.toString().isEmpty());
		assertTrue(compilerInvocationDiagnosticListener.kind != CompilerInvocationDiagnosticListener.NONE);
		if (!result.booleanValue()) {
			assertFalse("Compilation did not fail", false);
		}
		// check that the .class file exist for X
		assertTrue("delete failed", inputFile.delete());
		assertTrue("delete failed", packageFolder.delete());
	}

	public void testCompilerOneClassWithEclipseCompiler7() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File inputFile = new File(tmpFolder, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(
				"package p;\n" +
				"public class X extends File {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		// System compiler
		StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
		List<File> files = new ArrayList<File>();
		files.add(inputFile);
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
	
		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(tmpFolder);
		options.add("-sourcepath");
		options.add(tmpFolder);
		ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
		PrintWriter err = new PrintWriter(errBuffer);
		CompilerInvocationDiagnosticListener compilerInvocationDiagnosticListener = new CompilerInvocationDiagnosticListener(err) {
			@Override
			public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
				JavaFileObject source = diagnostic.getSource();
				assertNotNull("No source", source);
				super.report(diagnostic);
			}
		};
		CompilationTask task = compiler.getTask(null, manager, compilerInvocationDiagnosticListener, options, null, units);
		// check the classpath location
		Boolean result = task.call();
		err.flush();
		err.close();
		assertFalse(errBuffer.toString().isEmpty());
		assertTrue(compilerInvocationDiagnosticListener.kind != CompilerInvocationDiagnosticListener.NONE);
		if (!result.booleanValue()) {
			assertFalse("Compilation did not fail", false);
		}
		// check that the .class file exist for X
		assertTrue("delete failed", inputFile.delete());
	}

	// Test that JavaFileManager#inferBinaryName returns null for invalid file
	public void testInferBinaryName() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File dir = new File(tmpFolder, "src" + System.currentTimeMillis());
		dir.mkdirs();
		File inputFile = new File(dir, "test.txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write("This is not a valid Java file");
			writer.flush();
			writer.close();
		} catch (IOException e) {
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
		try {
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
	
			List<File> fins = new ArrayList<File>();
			fins.add(dir);
			JavaFileManager.Location sourceLoc = javax.tools.StandardLocation.SOURCE_PATH;
			fileManager.setLocation(sourceLoc, fins);
	
			Set<JavaFileObject.Kind> fileTypes = new HashSet<JavaFileObject.Kind>();
			fileTypes.add(JavaFileObject.Kind.OTHER);

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.list(sourceLoc, "", fileTypes, true);
			JavaFileObject invalid = null;
			for (JavaFileObject javaFileObject : compilationUnits) {
				invalid = javaFileObject;
				break;
			}
			String inferredName = fileManager.inferBinaryName(sourceLoc, invalid);
			fileManager.close();
			assertNull("Should return null for invalid file", inferredName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("delete failed", inputFile.delete());
		assertTrue("delete failed", dir.delete());
	}
	public void testFileManager() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File dir = new File(tmpFolder, "src" + System.currentTimeMillis());
		dir.mkdirs();
		File inputFile = new File(dir, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write("public class X {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		try {
			//JavaCompiler systemCompiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
	
			List<File> fins = new ArrayList<File>();
			fins.add(dir);
	
			JavaFileManager.Location sourceLoc = javax.tools.StandardLocation.SOURCE_PATH;
				fileManager.setLocation(sourceLoc, fins);
	
			Set<JavaFileObject.Kind> fileTypes = new HashSet<JavaFileObject.Kind>();
			fileTypes.add(JavaFileObject.Kind.SOURCE);
	
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.list(sourceLoc, "", fileTypes, true);
	
			Iterator<? extends JavaFileObject> it = compilationUnits.iterator();
			StringBuilder builder = new StringBuilder();
			while (it.hasNext()) {
				JavaFileObject next = it.next();
				String name = next.getName();
				name = name.replace('\\', '/');
				int lastIndexOf = name.lastIndexOf('/');
				builder.append(name.substring(lastIndexOf + 1));
			}
			assertEquals("Wrong contents", "X.java", String.valueOf(builder));
			
			List<File> files = new ArrayList<File>();
			files.add(dir);
			try {
				fileManager.getJavaFileObjectsFromFiles(files);
				fail("IllegalArgumentException should be thrown but not");
			} catch(IllegalArgumentException iae) {
				// Do nothing
			}
			
			fileManager.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// check that the .class file exist for X
		assertTrue("delete failed", inputFile.delete());
		assertTrue("delete failed", dir.delete());
	}
	public void testFileManager2() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File dir = new File(tmpFolder, "src" + System.currentTimeMillis());
		dir.mkdirs();
		File dir2 = new File(dir, "src2");
		dir2.mkdirs();
		File inputFile = new File(dir, "X.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write("public class X {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		File inputFile2 = new File(dir2, "X2.java");
		writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile2));
			writer.write("public class X2 {}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		try {
			//JavaCompiler systemCompiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
	
			List<File> fins = new ArrayList<File>();
			fins.add(dir);
	
			JavaFileManager.Location sourceLoc = javax.tools.StandardLocation.SOURCE_PATH;
				fileManager.setLocation(sourceLoc, fins);
	
			Set<JavaFileObject.Kind> fileTypes = new HashSet<JavaFileObject.Kind>();
			fileTypes.add(JavaFileObject.Kind.SOURCE);
	
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.list(sourceLoc, "", fileTypes, true);
	
			Iterator<? extends JavaFileObject> it = compilationUnits.iterator();
			List<String> names = new ArrayList<String>();
			while (it.hasNext()) {
				JavaFileObject next = it.next();
				String name = next.getName();
				name = name.replace('\\', '/');
				int lastIndexOf = name.lastIndexOf('/');
				names.add(name.substring(lastIndexOf + 1));
			}
			Collections.sort(names);
			assertEquals("Wrong contents", "[X.java, X2.java]", names.toString());
			fileManager.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// check that the .class file exist for X
		assertTrue("delete failed", inputFile2.delete());
		assertTrue("delete failed", dir2.delete());
		assertTrue("delete failed", inputFile.delete());
		assertTrue("delete failed", dir.delete());
	}
	
	
	public void testCompilerUnusedVariable() throws Exception {
		String tmpFolder = new File(System.getProperty("java.io.tmpdir")).getCanonicalPath();
		File inputFile = new File(tmpFolder, "NoWarn.java");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(
				"package p;\n" +
				"public class NoWarn {"
				+ "private String unused=\"testUnused\";}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// ignore
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		
		// System compiler
		StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());

		// create new list containing inputfile
		List<File> files = new ArrayList<File>();
		files.add(inputFile);
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(tmpFolder);
		
		//Add warnings to the compiler
		options.add("-warn:+unused");
		//Add nowarn to prevent warnings in the created directory
		options.add("-nowarn:["+ inputFile.getParent() + "]");
		
 		CompilationTask task = compiler.getTask(printWriter, manager, null, options, null, units);
		task.call();
		printWriter.flush();
		printWriter.close();
		
		//passing in the directory to no warn should ignore the path - resulting in no warnings.
		assertEquals("Expected no warnings to be generated.", "", stringWriter.toString());
	}
	/*
	 * Clean up the compiler
	 */
	public void testCleanUp() {
		compiler = null;
	}
}