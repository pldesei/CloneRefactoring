This clone method is located in File: 
org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/TypeAnnotationTest.java
The line range of this clone method is: 1231-1253
The content of this clone method is as follows:
	public void test032_field() throws Exception {
		this.runConformTest(
			new String[] {
				"A.java",
				"import java.lang.annotation.*;\n" + 
				"@Target(ElementType.TYPE_USE)\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@interface A {}\n",
				
				"X.java",
				"public class X {\n" + 
				"	@A int field;\n" +
				"}",
		},
		"");
		// javac-b81: Bytes:8[0 1 19 0 0 7 0 0]  19 = 0x13 (FIELD)
		String expectedOutput =
			"    RuntimeVisibleTypeAnnotations: \n" + 
			"      #8 @A(\n" + 
			"        target type = 0x13 FIELD\n" + 
			"      )\n";
		checkDisassembledClassFile(OUTPUT_DIR + File.separator + "X.class", "X", expectedOutput, ClassFileBytesDisassembler.SYSTEM);
	}
