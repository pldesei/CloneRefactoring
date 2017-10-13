This clone method is located in File: 
org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/TypeAnnotationTest.java
The line range of this clone method is: 1372-1394
The content of this clone method is as follows:
	public void test037_field() throws Exception {
		this.runConformTest(
			new String[] {
				"X.java",
				"public class X {\n" + 
				"	java.util.List<? extends @A Number> field;\n" +
				"}",
				"A.java",
				"import java.lang.annotation.*;\n" + 
				"@Target(ElementType.TYPE_USE)\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@interface A {}\n",
		},
		"");
		// javac-b81: Bytes:12[0 1 19 2 3 0 2 0 0 9 0 0]
		String expectedOutput =
			"    RuntimeVisibleTypeAnnotations: \n" + 
			"      #10 @A(\n" + 
			"        target type = 0x13 FIELD\n" + 
			"        location = [TYPE_ARGUMENT(0), WILDCARD]\n" +
			"      )\n";
		checkDisassembledClassFile(OUTPUT_DIR + File.separator + "X.class", "X", expectedOutput, ClassFileBytesDisassembler.SYSTEM);
	}
