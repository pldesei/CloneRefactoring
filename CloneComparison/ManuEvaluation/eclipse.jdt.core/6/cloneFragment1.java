This clone fragment is located in File: 
org.eclipse.jdt.core.tests.model/src/org/eclipse/jdt/core/tests/dom/ASTConverter15Test.java
The line range of this clone fragment is: 5275-5300
The content of this clone fragment is as follows:
    public void test0167() throws CoreException {
		ICompilationUnit sourceUnit = getCompilationUnit("Converter15" , "src", "test0167", "X.java"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runJLS3Conversion(sourceUnit, true, true);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.COMPILATION_UNIT);
		CompilationUnit compilationUnit = (CompilationUnit) result;
		assertProblemsSize(compilationUnit, 0);
		ASTNode node = getASTNode(compilationUnit, 1, 0);
    	assertEquals("Not a method declaration", ASTNode.METHOD_DECLARATION, node.getNodeType());
		MethodDeclaration methodDeclaration = (MethodDeclaration) node;
		List parameters = methodDeclaration.parameters();
		assertEquals("wrong size", 4, parameters.size());
		SingleVariableDeclaration param = (SingleVariableDeclaration)parameters.get(3);
		Type t = param.getType();
		String typeName = ((SimpleType)t).getName().getFullyQualifiedName();

		IType[] types = sourceUnit.getTypes();
		assertEquals("wrong size", 2, types.length);
		IType mainType = types[1];
		String[][] typeMatches = mainType.resolveType( typeName );
		assertNotNull(typeMatches);
		assertEquals("wrong size", 1, typeMatches.length);
		String[] typesNames = typeMatches[0];
		assertEquals("wrong size", 2, typesNames.length);
		assertEquals("Wrong part 1", "java.lang", typesNames[0]);
		assertEquals("Wrong part 2", "Object", typesNames[1]);
    }
