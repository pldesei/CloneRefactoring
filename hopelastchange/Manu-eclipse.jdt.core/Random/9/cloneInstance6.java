(startLine=808 endLine=833 srcPath=/root/Projects/newestVersion/eclipse.jdt.core/00001/eclipse.jdt.core/org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/TypeAnnotationTest.java)
	public void test022_classExtends() throws Exception {
		this.runConformTest(
			new String[] {
				"A.java",
				"import java.lang.annotation.*;\n" + 
				"@Target(ElementType.TYPE_USE)\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@interface A { }\n",
				
				"I.java",
				"interface I<T> {}\n",
				
				"X.java",
				"public class X implements I<String []@A[]> {}\n"
		},
		"");
		// javac-b81: Bytes:14[0 1 16 0 0 2 3 0 0 0 0 14 0 0] // type path: 2,3,0,0,0
		String expectedOutput =
				"  RuntimeVisibleTypeAnnotations: \n" + 
				"    #21 @A(\n" + 
				"      target type = 0x10 CLASS_EXTENDS\n" + 
				"      type index = 0\n" + 
				"      location = [TYPE_ARGUMENT(0), ARRAY]\n" + 
				"    )\n";
		checkDisassembledClassFile(OUTPUT_DIR + File.separator + "X.class", "X", expectedOutput, ClassFileBytesDisassembler.SYSTEM);
	}
