package waterloo.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;

import edu.pku.sei.codeclone.predictor.MyFragment;
import japa.parser.ASTParserTokenManager;
import japa.parser.JavaCharStream;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.Token;
import japa.parser.ast.CompilationUnit;
import mcidiff.util.ASTUtil;
import waterloo.History.GZTokenVisitor;

public class TokenCounter {
	private MyFragment frag;

	public TokenCounter(MyFragment frag) {
		this.frag = frag;
	}

	public static void main(String[] args) {
		int cnt = 0;
		try {
			FileInputStream in = new FileInputStream("/home/sonia/workspace/CloneAnalyzer/waterloo/FeatureExtractor.java");
			ASTParserTokenManager tokenmgr = new ASTParserTokenManager(new JavaCharStream(in));
			Token token = null; 
			
			while (!(token = tokenmgr.getNextToken()).image.equals("")) {
				//System.out.println(token.toString() + " " + token.image + " " + token.kind + " " + token.specialToken + " " + token.getValue());
				System.out.print(token.toString() + " ");
			}
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public int getTokens() {
		int cnt = 0;
		try {
			FileInputStream in = new FileInputStream(frag.getFilePath());
			ASTParserTokenManager tokenmgr = new ASTParserTokenManager(new JavaCharStream(in));
			Token token = null; 
			
			while (!(token = tokenmgr.getNextToken()).image.equals("")) {
				if (token.beginLine >= frag.getStartLine() && token.endLine <= frag.getEndLine())
					cnt ++;
				if (token.endLine > frag.getEndLine())
					break;
			}
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return cnt;
	}
	
	public int getTokenNum() {
		int tokenNum = 0;
		try {
			FileInputStream in = new FileInputStream(frag.getFilePath());
			CompilationUnit cu = JavaParser.parse(in);
			GZTokenVisitor visitor = new GZTokenVisitor();
			visitor.visit(cu, null);
			in.close();
			System.out.println("Frag:" + frag.getFilePath() + " " + frag.getStartLine() + "-" + frag.getEndLine());
			
			for (int i = frag.getStartLine(); i <= frag.getEndLine(); i++) {
				Vector<String> t = visitor.tokens.get(i);
				if (t == null || t.size() == 0)
					continue;
				else
					tokenNum += t.size();
				//System.out.println("Line:"+i);
				//System.out.println("TokensInThisLine:"+t.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("FragTokenNum:" + tokenNum);
		return tokenNum;
	}
}
