This clone instance is located in File: org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/eval/JDIStackFrame.java
The line range of this clone instance is: 240-255
The content of this clone instance is as follows:
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
