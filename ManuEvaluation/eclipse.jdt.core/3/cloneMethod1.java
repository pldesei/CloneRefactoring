This clone method is located in File: org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/problem/ProblemReporter.java
The line range of this clone method is: 8645-8659
The content of this clone method is as follows:
public void unsafeTypeConversion(Expression expression, TypeBinding expressionType, TypeBinding expectedType) {
	if (this.options.sourceLevel < ClassFileConstants.JDK1_5) return; // https://bugs.eclipse.org/bugs/show_bug.cgi?id=305259
	int severity = computeSeverity(IProblem.UnsafeTypeConversion);
	if (severity == ProblemSeverities.Ignore) return;
	if (!this.options.reportUnavoidableGenericTypeProblems && expression.forcedToBeRaw(this.referenceContext)) {
		return;
	}
	this.handle(
		IProblem.UnsafeTypeConversion,
		new String[] { new String(expressionType.readableName()), new String(expectedType.readableName()), new String(expectedType.erasure().readableName()) },
		new String[] { new String(expressionType.shortReadableName()), new String(expectedType.shortReadableName()), new String(expectedType.erasure().shortReadableName()) },
		severity,
		expression.sourceStart,
		expression.sourceEnd);
}
