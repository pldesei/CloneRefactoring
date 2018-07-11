public void bytecodeExceeds64KLimit(LambdaExpression location) {
	MethodBinding method = location.binding;
		this.handle(
			IProblem.BytecodeExceeds64KLimit,
			new String[] {new String(method.selector), typesAsString(method, false)},
			new String[] {new String(method.selector), typesAsString(method, true)},
			ProblemSeverities.Error | ProblemSeverities.Abort | ProblemSeverities.Fatal,
			location.sourceStart,
			location.diagnosticsSourceEnd());
}
