This clone method is located in File: 
org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/TypeAnnotationTest.java
The line range of this clone method is: 697-722
The content of this clone method is as follows:
	public void test018_classExtends() throws Exception {
		this.runConformTest(
			new String[] {
				"A.java",
				"import java.lang.annotation.*;\n" + 
				"@Target(ElementType.TYPE_USE)\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@interface A { }\n",

				"I.java",
				"interface I<T1,T2> {}\n",
				
				"X.java",
				"public class X implements I<Integer, @A String> {}\n"
		},
		"");
		// javac-b81: Bytes:12[0 1 16 0 0 1 3 1 0 14 0 0] // type path: 1,3,1
		String expectedOutput =
				"  RuntimeVisibleTypeAnnotations: \n" + 
				"    #21 @A(\n" + 
				"      target type = 0x10 CLASS_EXTENDS\n" + 
				"      type index = 0\n" + 
				"      location = [TYPE_ARGUMENT(1)]\n" + 
				"    )\n";
		checkDisassembledClassFile(OUTPUT_DIR + File.separator + "X.class", "X", expectedOutput, ClassFileBytesDisassembler.SYSTEM);
	}
