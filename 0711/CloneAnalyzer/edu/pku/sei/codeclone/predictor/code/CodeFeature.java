package edu.pku.sei.codeclone.predictor.code;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.List;

public class CodeFeature {
	public boolean followControlStat;// Feature 5
	public boolean containCompleteControlBlock;// Feature 11
	public int numCallStmt;// Feature 13
	public int numArithStmt;// Feature 14
	public boolean beginControlStat;// Feature 15

	public int cycComplexity;// Feature 12

	public int numTotalStmt;// For Feature 13 and 14
	
	public int numCall;//Another condition for Feature 13

	public CodeFeature() {
		followControlStat = false;
		containCompleteControlBlock = false;
		numCall = 0;
		numArithStmt = 0;
		beginControlStat = false;
	}

	public void handleControlStmt(Statement stmt, int stmtBodyStart, int start, int end) {
		if (stmtBodyStart != -1) {// ControlStmt but not BreakStmt, ContinueStmt
									// or ReturnStmt
			if (stmtBodyStart == start) {
				this.followControlStat = true;
				// System.out.println("Feature5:" + stmt.getBeginLine() + "-" +
				// stmt.getEndLine());
			}
			if (start <= stmt.getBeginLine() && stmt.getEndLine() <= end) {
				this.containCompleteControlBlock = true;		
				//System.out.println("Feature11:" + stmt.getBeginLine() + "-" + stmt.getEndLine());
			}
		}
		if (start == stmt.getBeginLine()) {
			this.beginControlStat = true;
			// Syste m.out.println("Feature15:" + stmt.getBeginLine() + "-" +
			// stmt.getEndLine());
		}
	}

	public void handleStmtNum(Statement stmt, int start, int end) {
		if (start <= stmt.getBeginLine() && stmt.getEndLine() <= end) {
			this.numTotalStmt++;
		}
	}

	public void handleCycComplexity(Statement stmt, int start, int end, int increase) {
		if (start <= stmt.getBeginLine() && stmt.getEndLine() <= end) {
			this.cycComplexity += increase;
		}
	}

	public boolean handleBinaryExpr(BinaryExpr binaryE) {
		Operator op = binaryE.getOperator();
		if (op.equals(BinaryExpr.Operator.plus) || op.equals(BinaryExpr.Operator.minus)
				|| op.equals(BinaryExpr.Operator.divide) || op.equals(BinaryExpr.Operator.times)
				|| op.equals(BinaryExpr.Operator.remainder)) {
			/*if(binaryE.getRight() instanceof IntegerLiteralExpr)
				System.out.println("integerLiteral:"+binaryE.toString());
			else if(binaryE.getRight() instanceof NameExpr)
				System.out.println("NameExpr:"+binaryE.toString());
			System.out.println(binaryE.toString() + " " + op);*/
			return true;
		}
		return false;
	}

}
