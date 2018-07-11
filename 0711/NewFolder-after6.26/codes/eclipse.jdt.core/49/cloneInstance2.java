	public void test003() throws JavaModelException {
    	this.workingCopy = getWorkingCopy("/Converter15/src/X.java", true/*resolve*/);
    	final String contents =
			"import java.util.Map;\r" +
			"public class X {\r" +
			"	Map<String, Number> map= null;\r" +
			"}\r";
    	ASTNode node = buildAST(
    			contents,
    			this.workingCopy,
    			false);
    	assertEquals("Not a compilation unit", ASTNode.COMPILATION_UNIT, node.getNodeType());
    	CompilationUnit compilationUnit = (CompilationUnit) node;
    	for (int i = 0, max = contents.length(); i < max; i++) {
    		final int lineNumber = compilationUnit.lineNumber(i);
    		final int columnNumber = compilationUnit.columnNumber(i);
    		final int position = compilationUnit.getPosition(lineNumber, columnNumber);
    		if (position == 0) {
    			assertEquals("Only true for first character", 0, i);
    		}
			assertEquals("Wrong char", contents.charAt(i), contents.charAt(position));
    	}
	}
