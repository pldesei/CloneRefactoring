	public Expression convert(org.eclipse.jdt.internal.compiler.ast.BinaryExpression expression) {
		InfixExpression infixExpression = new InfixExpression(this.ast);
		if (this.resolveBindings) {
			this.recordNodes(infixExpression, expression);
		}

		int expressionOperatorID = (expression.bits & org.eclipse.jdt.internal.compiler.ast.ASTNode.OperatorMASK) >> org.eclipse.jdt.internal.compiler.ast.ASTNode.OperatorSHIFT;
		infixExpression.setOperator(getOperatorFor(expressionOperatorID));

		if (expression.left instanceof org.eclipse.jdt.internal.compiler.ast.BinaryExpression
				&& ((expression.left.bits & org.eclipse.jdt.internal.compiler.ast.ASTNode.ParenthesizedMASK) == 0)) {
			// create an extended string literal equivalent => use the extended operands list
			infixExpression.extendedOperands().add(convert(expression.right));
			org.eclipse.jdt.internal.compiler.ast.Expression leftOperand = expression.left;
			org.eclipse.jdt.internal.compiler.ast.Expression rightOperand = null;
			do {
				rightOperand = ((org.eclipse.jdt.internal.compiler.ast.BinaryExpression) leftOperand).right;
				if ((((leftOperand.bits & org.eclipse.jdt.internal.compiler.ast.ASTNode.OperatorMASK) >> org.eclipse.jdt.internal.compiler.ast.ASTNode.OperatorSHIFT) != expressionOperatorID
							&& ((leftOperand.bits & org.eclipse.jdt.internal.compiler.ast.ASTNode.ParenthesizedMASK) == 0))
					 || ((rightOperand instanceof org.eclipse.jdt.internal.compiler.ast.BinaryExpression
				 			&& ((rightOperand.bits & org.eclipse.jdt.internal.compiler.ast.ASTNode.OperatorMASK) >> org.eclipse.jdt.internal.compiler.ast.ASTNode.OperatorSHIFT) != expressionOperatorID)
							&& ((rightOperand.bits & org.eclipse.jdt.internal.compiler.ast.ASTNode.ParenthesizedMASK) == 0))) {
				 	List extendedOperands = infixExpression.extendedOperands();
				 	InfixExpression temp = new InfixExpression(this.ast);
					if (this.resolveBindings) {
						this.recordNodes(temp, expression);
					}
				 	temp.setOperator(getOperatorFor(expressionOperatorID));
				 	Expression leftSide = convert(leftOperand);
					temp.setLeftOperand(leftSide);
					temp.setSourceRange(leftSide.getStartPosition(), leftSide.getLength());
					int size = extendedOperands.size();
				 	for (int i = 0; i < size - 1; i++) {
				 		Expression expr = temp;
				 		temp = new InfixExpression(this.ast);

						if (this.resolveBindings) {
							this.recordNodes(temp, expression);
						}
				 		temp.setLeftOperand(expr);
					 	temp.setOperator(getOperatorFor(expressionOperatorID));
						temp.setSourceRange(expr.getStartPosition(), expr.getLength());
				 	}
				 	infixExpression = temp;
				 	for (int i = 0; i < size; i++) {
				 		Expression extendedOperand = (Expression) extendedOperands.remove(size - 1 - i);
				 		temp.setRightOperand(extendedOperand);
				 		int startPosition = temp.getLeftOperand().getStartPosition();
				 		temp.setSourceRange(startPosition, extendedOperand.getStartPosition() + extendedOperand.getLength() - startPosition);
				 		if (temp.getLeftOperand().getNodeType() == ASTNode.INFIX_EXPRESSION) {
				 			temp = (InfixExpression) temp.getLeftOperand();
				 		}
				 	}
					int startPosition = infixExpression.getLeftOperand().getStartPosition();
					infixExpression.setSourceRange(startPosition, expression.sourceEnd - startPosition + 1);
					if (this.resolveBindings) {
						this.recordNodes(infixExpression, expression);
					}
					return infixExpression;
				}
				infixExpression.extendedOperands().add(0, convert(rightOperand));
				leftOperand = ((org.eclipse.jdt.internal.compiler.ast.BinaryExpression) leftOperand).left;
			} while (leftOperand instanceof org.eclipse.jdt.internal.compiler.ast.BinaryExpression && ((leftOperand.bits & org.eclipse.jdt.internal.compiler.ast.ASTNode.ParenthesizedMASK) == 0));
			Expression leftExpression = convert(leftOperand);
			infixExpression.setLeftOperand(leftExpression);
			infixExpression.setRightOperand((Expression)infixExpression.extendedOperands().remove(0));
			int startPosition = leftExpression.getStartPosition();
			infixExpression.setSourceRange(startPosition, expression.sourceEnd - startPosition + 1);
			return infixExpression;
		} else if (expression.left instanceof StringLiteralConcatenation
				&& ((expression.left.bits & org.eclipse.jdt.internal.compiler.ast.ASTNode.ParenthesizedMASK) == 0)
				&& (OperatorIds.PLUS == expressionOperatorID)) {
			StringLiteralConcatenation literal = (StringLiteralConcatenation) expression.left;
			final org.eclipse.jdt.internal.compiler.ast.Expression[] stringLiterals = literal.literals;
			infixExpression.setLeftOperand(convert(stringLiterals[0]));
			infixExpression.setRightOperand(convert(stringLiterals[1]));
			for (int i = 2; i < literal.counter; i++) {
				infixExpression.extendedOperands().add(convert(stringLiterals[i]));
			}
			infixExpression.extendedOperands().add(convert(expression.right));
			int startPosition = literal.sourceStart;
			infixExpression.setSourceRange(startPosition, expression.sourceEnd - startPosition + 1);
			return infixExpression;
		}
		Expression leftExpression = convert(expression.left);
		infixExpression.setLeftOperand(leftExpression);
		infixExpression.setRightOperand(convert(expression.right));
		int startPosition = leftExpression.getStartPosition();
		infixExpression.setSourceRange(startPosition, expression.sourceEnd - startPosition + 1);
		return infixExpression;
	}
