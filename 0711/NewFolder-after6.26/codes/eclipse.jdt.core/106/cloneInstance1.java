public org.eclipse.jdt.core.dom.CompilationUnit getAST3() throws JavaModelException {
	if (this.operation.astLevel != AST.JLS3 || !this.operation.resolveBindings) {
		// create AST (optionally resolving bindings)
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setCompilerOptions(this.workingCopy.getJavaProject().getOptions(true));
		if (JavaProject.hasJavaNature(this.workingCopy.getJavaProject().getProject()))
			parser.setResolveBindings(true);
		parser.setStatementsRecovery((this.operation.reconcileFlags & ICompilationUnit.ENABLE_STATEMENTS_RECOVERY) != 0);
		parser.setBindingsRecovery((this.operation.reconcileFlags & ICompilationUnit.ENABLE_BINDINGS_RECOVERY) != 0);
		parser.setSource(this.workingCopy);
		parser.setIgnoreMethodBodies((this.operation.reconcileFlags & ICompilationUnit.IGNORE_METHOD_BODIES) != 0);
		return (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST(this.operation.progressMonitor);
	}
	return this.operation.makeConsistent(this.workingCopy);
}
