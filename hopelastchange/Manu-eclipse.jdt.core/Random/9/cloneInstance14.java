(startLine=1396 endLine=1419 srcPath=/root/Projects/newestVersion/eclipse.jdt.core/00001/eclipse.jdt.core/org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/TypeAnnotationTest.java)
	public void test038_field() throws Exception {
		this.runConformTest(
			new String[] {
				"X.java",
				"class AA { class BB<T> {}}" + 
				"class X {\n" +
				"  AA.@A BB field;\n" +
				"}\n",

				"A.java",
				"import java.lang.annotation.*;\n" + 
				"@Target(ElementType.TYPE_USE)\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@interface A { }\n",
		},
		"");
		String expectedOutput =
				"    RuntimeVisibleTypeAnnotations: \n" + 
				"      #8 @A(\n" + 
				"        target type = 0x13 FIELD\n" + 
				"        location = [INNER_TYPE]\n" + 
				"      )\n";
		checkDisassembledClassFile(OUTPUT_DIR + File.separator + "X.class", "X", expectedOutput, ClassFileBytesDisassembler.SYSTEM);
	}
