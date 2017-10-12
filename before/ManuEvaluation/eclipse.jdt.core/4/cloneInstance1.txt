This clone instance is located in File: org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/problem/ProblemReporter.java
The line range of this clone instance is: 4967-4980
The content of this clone instance is as follows:
public void javadocDuplicatedThrowsClassName(TypeReference typeReference, int modifiers) {
	int severity = computeSeverity(IProblem.JavadocDuplicateThrowsClassName);
	if (severity == ProblemSeverities.Ignore) return;
	if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
		String[] arguments = new String[] {String.valueOf(typeReference.resolvedType.sourceName())};
		this.handle(
			IProblem.JavadocDuplicateThrowsClassName,
			arguments,
			arguments,
			severity,
			typeReference.sourceStart,
			typeReference.sourceEnd);
	}
}
