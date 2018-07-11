package edu.pku.sei.codeclone.predictor.code;

import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class MemberVisitor extends VoidVisitorAdapter<CodeStructure>{
	public void visit(FieldDeclaration expr, CodeStructure cs){
		for(VariableDeclarator vd : expr.getVariables()){
			cs.getFields().add(new Variable(expr.getType().toString(), vd.getId().getName()));
		}
	}

	public void visit(MethodDeclaration n, CodeStructure arg){
		arg.getMethods().add(new Method(n.getName(), n.getType().toString()));
	}

}
