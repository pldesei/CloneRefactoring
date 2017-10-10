(startLine=240 endLine=255 srcPath=/root/Projects/newestVersion/eclipse.jdt.core/00001/eclipse.jdt.core/org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/eval/JDIStackFrame.java)
public char[][] localVariableNames() {
	try {
		StackFrame frame = getStackFrame();
		Iterator variables = frame.visibleVariables().iterator();
		Vector names = new Vector();
		while (variables.hasNext()) {
			LocalVariable var = (LocalVariable)variables.next();
			names.addElement(var.name().toCharArray());
		}
		char[][] result = new char[names.size()][];
		names.copyInto(result);
		return result;
	} catch (AbsentInformationException e) {
		return null;
	}
}
