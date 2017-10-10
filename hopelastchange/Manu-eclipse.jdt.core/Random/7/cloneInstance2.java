(startLine=256 endLine=271 srcPath=/root/Projects/newestVersion/eclipse.jdt.core/00001/eclipse.jdt.core/org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/eval/JDIStackFrame.java)
public char[][] localVariableTypeNames() {
	try {
		StackFrame frame = getStackFrame();
		Iterator variables = frame.visibleVariables().iterator();
		Vector names = new Vector();
		while (variables.hasNext()) {
			LocalVariable var = (LocalVariable)variables.next();
			names.addElement(var.typeName().toCharArray());
		}
		char[][] result = new char[names.size()][];
		names.copyInto(result);
		return result;
	} catch (AbsentInformationException e) {
		return null;
	}
}
