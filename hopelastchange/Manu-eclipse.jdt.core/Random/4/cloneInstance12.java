(startLine=1347 endLine=1370 srcPath=/root/Projects/newestVersion/eclipse.jdt.core/00001/eclipse.jdt.core/org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/TypeAnnotationTest.java)
	public void test036_field() throws Exception {
		this.runConformTest(
			new String[] {
				"A.java",
				"import java.lang.annotation.*;\n" + 
				"@Target(ElementType.TYPE_USE)\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@interface A {}\n",
				
				"X.java",
				"public class X {\n" + 
				"	java.util.List<String[][]@A[][]> field;\n" +
				"}",
		},
		"");
		// javac-b81: Bytes:14[0 1 19 3 3 0 0 0 0 0 0 9 0 0]
		String expectedOutput =
			"    RuntimeVisibleTypeAnnotations: \n" + 
			"      #10 @A(\n" + 
			"        target type = 0x13 FIELD\n" + 
			"        location = [TYPE_ARGUMENT(0), ARRAY, ARRAY]\n" +
			"      )\n";
		checkDisassembledClassFile(OUTPUT_DIR + File.separator + "X.class", "X", expectedOutput, ClassFileBytesDisassembler.SYSTEM);
	}
