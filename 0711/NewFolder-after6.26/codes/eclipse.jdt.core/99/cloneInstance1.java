	public void testTryStatementWithResources4_since_4() throws Exception {

		createProject("P_17", JavaCore.VERSION_1_7);
		IPackageFragmentRoot currentSourceFolder = getPackageFragmentRoot("P_17", "src");

		try {
			IPackageFragment pack1 = currentSourceFolder.createPackageFragment("test0017", false, null);
			StringBuffer buf = new StringBuffer();
			buf.append("package test0017;\n");
			buf.append("\n");
			buf.append("public class X {\n");
			buf.append("	void foo() {\n");
			buf.append("		try (FileReader reader1 = new FileReader(\"file1\")) {\n");
			buf.append("			int ch;\n");
			buf.append("			while ((ch = reader1.read()) != -1) {\n");
			buf.append("				System.out.println(ch);\n");
			buf.append("			}\n");
			buf.append("		} finally {\n");
			buf.append("		}\n");
			buf.append("	}\n");
			buf.append("}");

			ICompilationUnit cu = pack1.createCompilationUnit("X.java", buf.toString(), false, null);
			CompilationUnit astRoot= createAST(cu, true, true);
			AST ast= astRoot.getAST();
			ASTRewrite rewrite= ASTRewrite.create(ast);

			Block block = ((MethodDeclaration) ((TypeDeclaration) astRoot.types().get(0)).bodyDeclarations().get(0)).getBody();
			List statements = block.statements();
			Statement statement = (Statement) statements.get(0);
			assertTrue(statement instanceof TryStatement);

			TryStatement tryStatement = (TryStatement) statement;

			VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
			internalSetExtraDimensions(fragment, 0);
			fragment.setName(ast.newSimpleName("reader2"));
			ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation();
			classInstanceCreation.setType(ast.newSimpleType(ast.newSimpleName("FileReader")));
			StringLiteral literal = ast.newStringLiteral();
			literal.setLiteralValue("file2");
			classInstanceCreation.arguments().add(literal);
			fragment.setInitializer(classInstanceCreation);
			VariableDeclarationExpression newVariableDeclarationExpression = ast.newVariableDeclarationExpression(fragment);
			newVariableDeclarationExpression.setType(ast.newSimpleType(ast.newSimpleName("FileReader")));

			ListRewrite listRewrite = rewrite.getListRewrite(tryStatement, TryStatement.RESOURCES_PROPERTY);
			listRewrite.insertLast(newVariableDeclarationExpression, null);

			Document document1= new Document(cu.getSource());
			TextEdit res= rewrite.rewriteAST(document1, null);
			res.apply(document1);
			String preview = document1.get();
			
			buf= new StringBuffer();
			buf.append("package test0017;\n");
			buf.append("\n");
			buf.append("public class X {\n");
			buf.append("	void foo() {\n");
			buf.append("		try (FileReader reader1 = new FileReader(\"file1\");\n");
			buf.append("				FileReader reader2 = new FileReader(\"file2\")) {\n");
			buf.append("			int ch;\n");
			buf.append("			while ((ch = reader1.read()) != -1) {\n");
			buf.append("				System.out.println(ch);\n");
			buf.append("			}\n");
			buf.append("		} finally {\n");
			buf.append("		}\n");
			buf.append("	}\n");
			buf.append("}");
			assertEqualString(preview, buf.toString());
		} finally {
			deleteProject("P_17");
		}
	}
