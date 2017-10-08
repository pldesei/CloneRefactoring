This clone instance is located in File: org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/TypeAnnotationTest.java
The line range of this clone instance is: 1255-1278
The content of this clone instance is as follows:
	public void test033_field() throws Exception {
		this.runConformTest(
			new String[] {
				"A.java",
				"import java.lang.annotation.*;\n" + 
				"@Target(ElementType.TYPE_USE)\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@interface A {}\n",
				
				"X.java",
				"public class X {\n" + 
				"	java.util.List<@A String> field;\n" +
				"}",
		},
		"");
		// javac-b81: Bytes:10[0 1 19 1 3 0 0 9 0 0]
		String expectedOutput =
			"    RuntimeVisibleTypeAnnotations: \n" + 
			"      #10 @A(\n" + 
			"        target type = 0x13 FIELD\n" + 
			"        location = [TYPE_ARGUMENT(0)]\n" + 
			"      )\n";
		checkDisassembledClassFile(OUTPUT_DIR + File.separator + "X.class", "X", expectedOutput, ClassFileBytesDisassembler.SYSTEM);
	}
