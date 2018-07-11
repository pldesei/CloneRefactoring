package waterloo.HistoryLocation;

import java.util.HashMap;
import java.util.Vector;

import japa.parser.ast.BlockComment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.LineComment;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EmptyMemberDeclaration;
import japa.parser.ast.body.EmptyTypeDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
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
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class GZTokenVisitor extends VoidVisitorAdapter<Void> {
	public HashMap<Integer, Vector<String>> tokens = new HashMap<Integer, Vector<String>>();
	public HashMap<Integer, Integer> realTokenNum = new HashMap<Integer, Integer>();
	
	@Override
    public void visit(AnnotationDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("AnnotationDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(AnnotationMemberDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("AnnotationMemberDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ArrayAccessExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ArrayAccessExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ArrayCreationExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ArrayCreationExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ArrayInitializerExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ArrayInitializerExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(AssertStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("AssertStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(AssignExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("AssignExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(BinaryExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("BinaryExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(BlockComment n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("BlockComment");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(BlockStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("BlockStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
        
    }

	@Override
    public void visit(BooleanLiteralExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("BooleanLiteralExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(BreakStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("BreakStmt");
		tokens.put(n.getBeginLine(), tmp);
		if (realTokenNum.containsKey(startLine))
			realTokenNum.put(startLine, realTokenNum.get(startLine) + 1);
		else {
			realTokenNum.put(startLine, 1);
		}
        super.visit(n, arg);
    }

	@Override
    public void visit(CastExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("CastExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(CatchClause n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("CatchClause");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(CharLiteralExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("CharLiteralExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ClassExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ClassExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ClassOrInterfaceDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ClassOrInterfaceType n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ClassOrInterfaceType");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(CompilationUnit n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("CompilationUnit");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ConditionalExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ConditionalExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ConstructorDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ConstructorDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ContinueStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ContinueStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(DoStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("DoStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(DoubleLiteralExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("DoubleLiteralExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(EmptyMemberDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("EmptyMemberDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(EmptyStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("EmptyStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(EmptyTypeDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("EmptyTypeDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(EnclosedExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("EnclosedExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(EnumConstantDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("EnumConstantDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(EnumDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("EnumDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ExplicitConstructorInvocationStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ExplicitConstructorInvocationStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ExpressionStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ExpressionStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(FieldAccessExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("FieldAccessExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(FieldDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("FieldDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ForeachStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ForeachStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ForStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ForStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(IfStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("IfStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ImportDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ImportDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(InitializerDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("InitializerDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(InstanceOfExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("InstanceOfExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(IntegerLiteralExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("IntegerLiteralExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(IntegerLiteralMinValueExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("IntegerLiteralMinValueExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(JavadocComment n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("JavadocComment");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(LabeledStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("LabeledStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(LineComment n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("LineComment");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(LongLiteralExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("LongLiteralExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(LongLiteralMinValueExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("LongLiteralMinValueExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(MarkerAnnotationExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("MarkerAnnotationExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(MemberValuePair n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("MemberValuePair");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(MethodCallExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("MethodCallExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(MethodDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("MethodDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(NameExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("NameExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(NormalAnnotationExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("NormalAnnotationExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(NullLiteralExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("NullLiteralExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ObjectCreationExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ObjectCreationExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(PackageDeclaration n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("PackageDeclaration");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(Parameter n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("Parameter");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(PrimitiveType n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("PrimitiveType");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(QualifiedNameExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("QualifiedNameExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ReferenceType n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ReferenceType");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ReturnStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ReturnStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(SingleMemberAnnotationExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("SingleMemberAnnotationExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(StringLiteralExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("StringLiteralExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(SuperExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("SuperExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(SwitchEntryStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("SwitchEntryStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(SwitchStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("SwitchStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(SynchronizedStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("SynchronizedStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);

    }

	@Override
    public void visit(ThisExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ThisExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(ThrowStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("ThrowStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(TryStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("TryStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(TypeDeclarationStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("TypeDeclarationStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(TypeParameter n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("TypeParameter");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(UnaryExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("UnaryExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(VariableDeclarationExpr n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("VariableDeclarationExpr");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(VariableDeclarator n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("VariableDeclarator");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(VariableDeclaratorId n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("VariableDeclaratorId");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(VoidType n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("VoidType");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(WhileStmt n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("WhileStmt");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }

	@Override
    public void visit(WildcardType n, Void arg) {
		int startLine = n.getBeginLine();
		Vector<String> tmp = new Vector<String>();
		if (tokens.containsKey(startLine)) {
			tmp = tokens.get(startLine);
		}
		tmp.addElement("WildcardType");
		tokens.put(n.getBeginLine(), tmp);
        super.visit(n, arg);
    }
}
