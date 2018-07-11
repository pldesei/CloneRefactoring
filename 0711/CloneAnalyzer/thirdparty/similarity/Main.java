package thirdparty.similarity;

import java.io.File;
import java.io.IOException;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

public class Main {
    public static void main(String args[]){
        File f = new File("C:/personal/CodeClonePrediction_TSE/data/jabref/jabref-all/0664/jabref/src/java/net/sf/jabref/search/SearchExpressionParser.java");
        try {
            CompilationUnit cu = JavaParser.parse(f);
            System.out.println(cu.getImports());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
