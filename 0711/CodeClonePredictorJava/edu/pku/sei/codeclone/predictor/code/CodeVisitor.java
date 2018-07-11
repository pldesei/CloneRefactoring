package edu.pku.sei.codeclone.predictor.code;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class CodeVisitor extends VoidVisitorAdapter<CodeStructure>{
	private int start;
	private int end;
    
	public CodeVisitor(int start, int end) {
	    this.start = start;
	    this.end = end;
	}
	public void visit(ImportDeclaration importDec, CodeStructure cs){
		cs.getImports().add(importDec);
	}
	public void visit(MethodCallExpr n, CodeStructure arg){
	    if(n.getBeginLine()>this.end||n.getEndLine()<this.start){
	        return;
	    }
		arg.numCall++;
		arg.handleInvoke(n);
        if (n.getScope() != null) {
            n.getScope().accept(this, arg);
        }
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
	public void visit(NameExpr expr, CodeStructure cs){
	    if(expr.getBeginLine()>this.end||expr.getEndLine()<this.start){
            return;
        }
        
	    cs.handleVariable(expr);
	}
	public void visit(FieldAccessExpr expr, CodeStructure cs){
	    if(expr.getBeginLine()>this.end||expr.getEndLine()<this.start){
            return;
        }
	    cs.numFieldRef++;
	}

	public void visit(VariableDeclarationExpr expr, CodeStructure cs){
		for(VariableDeclarator vd : expr.getVars()){
			cs.getLocals().add(new Variable(expr.getType().toString(), vd.getId().getName()));
		}
	}
	public void visit(FieldDeclaration expr, CodeStructure cs){
		for(VariableDeclarator vd : expr.getVariables()){
			cs.getFields().add(new Variable(expr.getType().toString(), vd.getId().getName()));
		}
	}

	public void visit(MethodDeclaration n, CodeStructure arg){
	    if(n.getBeginLine()>this.end||n.getEndLine()<this.start){
            return;
        }
	    arg.currentMethod = n;
		arg.getLocals().clear();
		
        if (n.getBody() != null) {
            n.getBody().accept(this, arg);
        }
	}
    public void visit(ConstructorDeclaration n, CodeStructure arg){
        if(n.getBeginLine()>this.end||n.getEndLine()<this.start){
            return;
        }
        arg.currentMethod = n;
        arg.getLocals().clear();
        
        n.getBlock().accept(this, arg);
    }
	
}
