	} else {
		this.handle(
			IProblem.BytecodeExceeds64KLimit,
			new String[] {new String(location.selector), typesAsString(method, false)},
			new String[] {new String(location.selector), typesAsString(method, true)},
			ProblemSeverities.Error | ProblemSeverities.Abort | ProblemSeverities.Fatal,
			location.sourceStart,
			location.sourceEnd);
	}
