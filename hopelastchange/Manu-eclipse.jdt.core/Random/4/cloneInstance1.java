(startLine=671 endLine=695 srcPath=/root/Projects/newestVersion/eclipse.jdt.core/00001/eclipse.jdt.core/org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/TypeAnnotationTest.java)
	public void test017_classExtends() throws Exception {
		this.runConformTest(
			new String[] {
				"Marker.java",
				"import java.lang.annotation.*;\n" + 
				"@Target(ElementType.TYPE_USE)\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@interface Marker { }\n",
				"I.java",
				"interface I<T> {}\n",
				"X.java",
				"public class X implements I<@Marker String> {\n" + 
				"}",
		},
		"");
		// javac-b81: Bytes:12[0 1 16 0 0 1 3 0 0 14 0 0] // type path: 1,3,0
		String expectedOutput =
				"  RuntimeVisibleTypeAnnotations: \n" + 
				"    #21 @Marker(\n" + 
				"      target type = 0x10 CLASS_EXTENDS\n" + 
				"      type index = 0\n" + 
				"      location = [TYPE_ARGUMENT(0)]\n" + 
				"    )\n";
		checkDisassembledClassFile(OUTPUT_DIR + File.separator + "X.class", "X", expectedOutput, ClassFileBytesDisassembler.SYSTEM);
	}
