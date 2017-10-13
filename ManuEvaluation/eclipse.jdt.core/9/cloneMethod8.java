This clone method is located in File: 
org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/TypeAnnotationTest.java
The line range of this clone method is: 926-947
The content of this clone method is as follows:
	public void test025_classTypeParameterBound() throws Exception {
		this.runConformTest(
			new String[] {
				"X.java",
				"public class X<T extends @A String> {}",
				"A.java",
				"import java.lang.annotation.*;\n" + 
				"@Target(ElementType.TYPE_USE)\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@interface A {}\n"
		},
		"");
		// javac-b81: Bytes:10[0 1 17 0 0 0 0 13 0 0] 
		// [17 0 0] is CLASS_PARAMETER_BOUND type_parameter_index=0 bound_index=0
		String expectedOutput =
			"  RuntimeVisibleTypeAnnotations: \n" + 
			"    #21 @A(\n" + 
			"      target type = 0x11 CLASS_TYPE_PARAMETER_BOUND\n" + 
			"      type parameter index = 0 type parameter bound index = 0\n" + 
			"    )\n";
		checkDisassembledClassFile(OUTPUT_DIR + File.separator + "X.class", "X", expectedOutput, ClassFileBytesDisassembler.SYSTEM);
	}
