This clone instance is located in File: org.eclipse.jdt.core.tests.model/src/org/eclipse/jdt/core/tests/dom/ASTConverterAST4Test.java
The line range of this clone instance is: 1845-1867
The content of this clone instance is as follows:
	public void test0085() throws JavaModelException {
		ICompilationUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0085", "Test.java"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(getJLS4(), sourceUnit, false);
		ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
		assertNotNull("Expression should not be null", node); //$NON-NLS-1$
		ForStatement forStatement = this.ast.newForStatement();
		VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
		variableDeclarationFragment.setName(this.ast.newSimpleName("i")); //$NON-NLS-1$
		variableDeclarationFragment.setInitializer(this.ast.newNumberLiteral("0"));//$NON-NLS-1$

		VariableDeclarationExpression variableDeclarationExpression = this.ast.newVariableDeclarationExpression(variableDeclarationFragment);
		variableDeclarationExpression.setType(this.ast.newPrimitiveType(PrimitiveType.INT));

		forStatement.initializers().add(variableDeclarationExpression);
		PostfixExpression postfixExpression = this.ast.newPostfixExpression();
		postfixExpression.setOperand(this.ast.newSimpleName("i"));//$NON-NLS-1$
		postfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
		forStatement.updaters().add(postfixExpression);
		forStatement.setBody(this.ast.newEmptyStatement());
		assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));		//$NON-NLS-1$
		checkSourceRange(node, "for (int i = 0;; i++);", source); //$NON-NLS-1$
	}
