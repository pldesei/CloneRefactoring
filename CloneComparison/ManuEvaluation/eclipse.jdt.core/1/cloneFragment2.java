This clone fragment is located in File: 
org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/NullAnnotationBatchCompilerTest.java
The line range of this clone fragment is: 518-598
The content of this clone fragment is as follows:
	public void testBug466291b() {
		this.runConformTest(
			new String[] {
					"p/X.java",
					"package p;\n" +
					"import static java.lang.annotation.ElementType.*;\n" +
					"import java.lang.annotation.*;\n" +
					"@NonNullByDefault\n" +
					"public class X {\n" +
					"  public Object foo(@Nullable Object o, Object o2) {\n" +
					"	 return new Object();\n" +
					"  }\n" +
					"  public Object bar() {\n" +
					"	 return this;\n" +
					"  }\n" +
					"}\n" +
					"@Documented\n" +
					"@Retention(RetentionPolicy.CLASS)\n" +
					"@Target({ METHOD, PARAMETER })\n" +
					"@interface NonNull{\n" +
					"}\n" +
					"@Documented\n" +
					"@Retention(RetentionPolicy.CLASS)\n" +
					"@Target({ METHOD, PARAMETER })\n" +
					"@interface Nullable{\n" +
					"}\n" +
					"@Documented\n" +
					"@Retention(RetentionPolicy.CLASS)\n" +
					"@Target({ PACKAGE, TYPE, METHOD, CONSTRUCTOR })\n" +
					"@interface NonNullByDefault{\n" +
					"}"
			},
			"\"" + OUTPUT_DIR +  File.separator + "p" + File.separator + "X.java\""
			+ " -1.5"
			+ " -warn:+nullAnnot(p.Nullable|p.NonNull|p.NonNullByDefault) -warn:+null -warn:-nullUncheckedConversion "
			+ "-proc:none -d \"" + OUTPUT_DIR + "\"",
			"",
			"",
			true);

		// force reading of BinaryTypeBinding(p.X):
		String xPath = OUTPUT_DIR + File.separator + "p" + File.separator + "X.java";
		new File(xPath).delete();
			this.runNegativeTest(
				new String[] {
						"p2/X2.java",
						"package p2;\n" +
						"import org.eclipse.jdt.annotation.*;\n" +
						"public class X2 {\n" +
						"  @NonNull Object test(@NonNull p.X nonnullX, @Nullable p.X nullableX) {\n" +
						"    nonnullX.foo(nullableX, nullableX);\n" +
						"	 return nonnullX.bar();\n" +
						"  }\n" +
						"}\n",
						"org/eclipse/jdt/annotation/NonNull.java",
						NONNULL_ANNOTATION_CONTENT,
						"org/eclipse/jdt/annotation/Nullable.java",
						NULLABLE_ANNOTATION_CONTENT,
						"org/eclipse/jdt/annotation/NonNullByDefault.java",
						NONNULL_BY_DEFAULT_ANNOTATION_CONTENT
				},
				"\"" + OUTPUT_DIR +  File.separator + "p2" + File.separator + "X2.java\""
				+ " -sourcepath \"" + OUTPUT_DIR + "\""
				+ " -classpath \"" + OUTPUT_DIR + "\""
				+ " -1.5"
				+ " -warn:+nullAnnot(org.eclipse.jdt.annotation.Nullable|org.eclipse.jdt.annotation.NonNull|org.eclipse.jdt.annotation.NonNullByDefault)"
				+ " -warn:+nullAnnot(|x.AbsentNonNull|) "
				+ " -warn:+nullAnnot(p.Nullable||p.NonNullByDefault) "
				+ " -warn:+nullAnnot(yet.AnotherNullable|yet.AnotherNonnull|yet.anotherNNBD) "
				+ " -warn+null -proc:none -d \"" + OUTPUT_DIR + "\"",
				"",
				"----------\n" +
				"1. ERROR in ---OUTPUT_DIR_PLACEHOLDER---/p2/X2.java (at line 5)\n" +
				"	nonnullX.foo(nullableX, nullableX);\n" +
				"	                        ^^^^^^^^^\n" +
				"Null type mismatch: required '@NonNull Object' but the provided value is specified as @Nullable\n" +
				"----------\n" +
				"1 problem (1 error)\n",
				false);
	}
