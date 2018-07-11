protected void consumeInternalCompilationUnit() {
	// InternalCompilationUnit ::= PackageDeclaration
	// InternalCompilationUnit ::= PackageDeclaration ImportDeclarations ReduceImports
	// InternalCompilationUnit ::= ImportDeclarations ReduceImports
	if (this.compilationUnit.isPackageInfo()) {
		this.compilationUnit.types = new TypeDeclaration[1];
		// create a fake interface declaration
		TypeDeclaration declaration = new TypeDeclaration(this.compilationUnit.compilationResult);
		declaration.name = TypeConstants.PACKAGE_INFO_NAME;
		declaration.modifiers = ClassFileConstants.AccDefault | ClassFileConstants.AccInterface;
		this.compilationUnit.types[0] = declaration;
		declaration.javadoc = this.compilationUnit.javadoc;
	}
}
