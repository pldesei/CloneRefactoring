This clone method is located in File: 
org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/BatchCompilerTest.java
The line range of this clone method is: 2505-2550
The content of this clone method is as follows:
public void test040(){
	this.runConformTest(
		new String[] {
			"p/X.java",
			"package p;\n" +
			"/** */\n" +
			"public class X {\n" +
			"}",
			"p/Z.java",
			"package p;\n" +
			"/** */\n" +
			"public class Z {\n" +
			"}"
		},
        "\"" + OUTPUT_DIR +  File.separator + "p"  +  File.separator + "X.java\""
        + " \"" + OUTPUT_DIR +  File.separator + "p"  +  File.separator + "Z.java\""
        + " -1.5 -g -preserveAllLocals"
        + " -nowarn"
        + " -proceedOnError -referenceInfo -d \"" + OUTPUT_DIR + "\"",
        "",
        "",
        true);
	this.runConformTest(
		new String[] {
			"Y.java",
			"/** */\n" +
			"public class Y {\n" +
			"  p.X x;\n" +
			"  p.Z z;\n" +
			"}",
		},
        "\"" + OUTPUT_DIR +  File.separator + "Y.java\""
        + " -1.5 -g -preserveAllLocals"
        + " -cp \"" + OUTPUT_DIR + "[+p/X" + File.pathSeparator + "-p/*]\""
        + " -warn:+deprecation,syntheticAccess,uselessTypeCheck,unsafe,finalBound,unusedLocal"
        + " -proceedOnError -referenceInfo -d \"" + OUTPUT_DIR + "\"",
        "",
        "----------\n" + 
        "1. WARNING in ---OUTPUT_DIR_PLACEHOLDER---/Y.java (at line 4)\n" + 
        "	p.Z z;\n" + 
        "	^^^\n" + 
        "Access restriction: The type \'Z\' is not API (restriction on classpath entry \'---OUTPUT_DIR_PLACEHOLDER---\')\n" + 
        "----------\n" + 
        "1 problem (1 warning)\n",
        false);
}
