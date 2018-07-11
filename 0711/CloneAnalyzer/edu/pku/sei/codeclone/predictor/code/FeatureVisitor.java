package edu.pku.sei.codeclone.predictor.code;

import java.util.List;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class FeatureVisitor extends VoidVisitorAdapter<CodeFeature> {
	private int start;
	private int end;

	public FeatureVisitor(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public void visit(MethodCallExpr n, CodeFeature arg) {
		if (start <= n.getBeginLine() && n.getEndLine() <= end) {
			//System.out.println("MethodCall:"+n.toString()+" "+n.getBeginLine());
			arg.numCall++;
			if (n.getScope() != null)
				n.getScope().accept(this, arg);

			if (n.getTypeArgs() != null) {
				for (Type t : n.getTypeArgs()) {
					t.accept(this, arg);
				}
			}
			if (n.getArgs() != null) {
				for (Expression e : n.getArgs()) {
					e.accept(this, arg);
				}
			}
		}
	}

	public void visit(AssertStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		if (n.getCheck() != null)
			n.getCheck().accept(this, arg);
		if (n.getMessage() != null)
			n.getMessage().accept(this, arg);

	}

	public void visit(BlockStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		if (n.getStmts() != null) {
			for (Statement stmt : n.getStmts()) {
				stmt.accept(this, arg);
			}
		}
		
	}

	public void visit(EmptyStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);

	}

	public void visit(ExplicitConstructorInvocationStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		if (n.getArgs() != null) {
			for (Expression e : n.getArgs()) {
				e.accept(this, arg);
			}
		}
		if (n.getExpr() != null)
			n.getExpr().accept(this, arg);
		if (n.getTypeArgs() != null) {
			for (Type type : n.getTypeArgs()) {
				type.accept(this, arg);
			}
		}
	}

	public void visit(ExpressionStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		Expression e=n.getExpression();
		boolean isArithStmt=false;
		if(n.getBeginLine()>=this.start&&n.getEndLine()<=this.end){
			 //System.out.println("Expression:"+n.getExpression().toString()+" "+n.getBeginLine()); 
				if(e instanceof AssignExpr){ 
					AssignExpr assE=(AssignExpr) e;
					//System.out.println(assE.getOperator()+" "+assE.toString());
					if(assE.getValue() instanceof BinaryExpr){
						BinaryExpr binaryE=(BinaryExpr)assE.getValue();
						isArithStmt=isArithStmt||arg.handleBinaryExpr(binaryE);
					}
						
				}else if(e instanceof VariableDeclarationExpr){
					VariableDeclarationExpr varDecExpr=(VariableDeclarationExpr)e;
					List<VariableDeclarator> vars=varDecExpr.getVars();
					for(VariableDeclarator var:vars){
						if(var.getInit() instanceof BinaryExpr){
							BinaryExpr binaryE=(BinaryExpr)var.getInit();
							isArithStmt=isArithStmt||arg.handleBinaryExpr(binaryE);	
						}							
					}
				}else if(e instanceof MethodCallExpr){
					// Feature 13
					arg.numCallStmt++;
				}
		}	
		if(isArithStmt) arg.numArithStmt++;
		if(n.getExpression()!=null)
			n.getExpression().accept(this, arg);
	}

	public void visit(LabeledStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		if (n.getStmt() != null)
			n.getStmt().accept(this, arg);
	}

	public void visit(SwitchEntryStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		if (n.getLabel() != null)
			n.getLabel().accept(this, arg);
		if (n.getStmts() != null) {
			for (Statement stmt : n.getStmts()) {
				stmt.accept(this, arg);
			}
		}
	}

	public void visit(SynchronizedStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		if (n.getBlock() != null)
			n.getBlock().accept(this, arg);
		if (n.getExpr() != null)
			n.getExpr().accept(this, arg);
	}

	public void visit(ThrowStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		arg.handleCycComplexity(n, this.start, this.end, 1);
		if (n.getExpr() != null)
			n.getExpr().accept(this, arg);
	}

	public void visit(TryStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		int increase =0;
		if(n.getCatchs()!=null)		
				increase=n.getCatchs().size();
		if (n.getFinallyBlock() != null)
			increase++;
		arg.handleCycComplexity(n, this.start, this.end, increase);

		if (n.getTryBlock() != null)
			n.getTryBlock().accept(this, arg);
		if(n.getCatchs()!=null){
			List<CatchClause> catchClauseList=n.getCatchs();
			for(CatchClause catchClause:catchClauseList)
				catchClause.accept(this, arg);		
		}
		if (n.getFinallyBlock() != null)
			n.getFinallyBlock().accept(this, arg);


	}

	public void visit(TypeDeclarationStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		if (n.getTypeDeclaration() != null)
			n.getTypeDeclaration().accept(this, arg);
	}

	public void visit(IfStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		int increase = 0;
		if (n.getThenStmt() != null)
			increase++;
		if (n.getElseStmt() != null)
			increase++;
		arg.handleCycComplexity(n, this.start, this.end, increase);

		int stmtBodyStart = n.getThenStmt().getBeginLine();
		arg.handleControlStmt(n, stmtBodyStart, this.start, this.end);

		if (n.getCondition() != null)
			n.getCondition().accept(this, arg);
		if (n.getThenStmt() != null)
			n.getThenStmt().accept(this, arg);
		if (n.getElseStmt() != null)
			n.getElseStmt().accept(this, arg);
	}

	public void visit(SwitchStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		int increase = n.getEntries().size();
		arg.handleCycComplexity(n, this.start, this.end, increase);

		int stmtBodyStart = n.getSelector().getEndLine() + 1;
		arg.handleControlStmt(n, stmtBodyStart, this.start, this.end);

		if (n.getSelector() != null)
			n.getSelector().accept(this, arg);
		if (n.getEntries() != null) {
			for (Statement stmt : n.getEntries()) {
				stmt.accept(this, arg);
			}
		}
	}

	public void visit(ForStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		arg.handleCycComplexity(n, this.start, this.end, 1);

		int stmtBodyStart = n.getBody().getBeginLine();
		arg.handleControlStmt(n, stmtBodyStart, this.start, this.end);

		if (n.getInit() != null) {
			for (Expression exp : n.getInit()) {
				exp.accept(this, arg);
			}
		}
		if (n.getCompare() != null)
			n.getCompare().accept(this, arg);
		if (n.getUpdate() != null) {
			for (Expression e : n.getUpdate()) {
				e.accept(this, arg);
			}
		}
		if (n.getBody() != null)
			n.getBody().accept(this, arg);
	}

	public void visit(ForeachStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		arg.handleCycComplexity(n, this.start, this.end, 1);

		int stmtBodyStart = n.getBody().getBeginLine();
		arg.handleControlStmt(n, stmtBodyStart, this.start, this.end);

		if (n.getVariable() != null)
			n.getVariable().accept(this, arg);
		if (n.getIterable() != null)
			n.getIterable().accept(this, arg);
		if (n.getBody() != null)
			n.getBody().accept(this, arg);
	}

	public void visit(WhileStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		arg.handleCycComplexity(n, this.start, this.end, 1);

		int stmtBodyStart = n.getBody().getBeginLine();
		arg.handleControlStmt(n, stmtBodyStart, this.start, this.end);

		if (n.getCondition() != null)
			n.getCondition().accept(this, arg);
		if (n.getBody() != null)
			n.getBody().accept(this, arg);
	}

	public void visit(DoStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		arg.handleCycComplexity(n, this.start, this.end, 1);

		int stmtBodyStart = n.getBody().getBeginLine();
		arg.handleControlStmt(n, stmtBodyStart, this.start, this.end);

		if (n.getCondition() != null)
			n.getCondition().accept(this, arg);
		if (n.getBody() != null)
			n.getBody().accept(this, arg);
	}

	public void visit(BreakStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		arg.handleCycComplexity(n, this.start, this.end, 1);

		int stmtBodyStart = -1;
		arg.handleControlStmt(n, stmtBodyStart, this.start, this.end);
	}

	public void visit(ContinueStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		arg.handleCycComplexity(n, this.start, this.end, 1);
		int stmtBodyStart = -1;
		arg.handleControlStmt(n, stmtBodyStart, this.start, this.end);
	}

	public void visit(ReturnStmt n, CodeFeature arg) {
		arg.handleStmtNum(n, this.start, this.end);
		arg.handleCycComplexity(n, this.start, this.end, 1);

		int stmtBodyStart = -1;
		arg.handleControlStmt(n, stmtBodyStart, this.start, this.end);

		if (n.getExpr() != null)
			n.getExpr().accept(this, arg);
	}
}