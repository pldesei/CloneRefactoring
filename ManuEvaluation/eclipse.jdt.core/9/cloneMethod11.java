This clone method is located in File: 
org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/TypeAnnotationTest.java
The line range of this clone method is: 1322-1345
The content of this clone method is as follows:
	public void test035_field() throws Exception {
		this.runConformTest(
			new String[] {
				"A.java",
				"import java.lang.annotation.*;\n" + 
				"@Target(ElementType.TYPE_USE)\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@interface A {}\n",
				
				"X.java",
				"public class X {\n" + 
				"	java.util.Map<String, @A String> field;\n" +
				"}",
		},
		"");
		// javac-b81: Bytes:10[0 1 19 1 3 1 0 9 0 0]
		String expectedOutput =
			"    RuntimeVisibleTypeAnnotations: \n" + 
			"      #10 @A(\n" + 
			"        target type = 0x13 FIELD\n" + 
			"        location = [TYPE_ARGUMENT(1)]\n" +
			"      )\n";
		checkDisassembledClassFile(OUTPUT_DIR + File.separator + "X.class", "X", expectedOutput, ClassFileBytesDisassembler.SYSTEM);
	}
