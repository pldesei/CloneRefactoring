package edu.pku.sei.codeclone.predictor.code;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import japa.parser.ast.body.Parameter;

public class MethodVisitor extends VoidVisitorAdapter<Object>{
	private Method m;
	private int start;
	private int end;
	public MethodVisitor(int start, int end) {
		this.start = start;
		this.end = end;
	}
	private boolean hasInterception(int methodStart, int methodEnd){
	    if(methodEnd > start && methodEnd < end){
	        return true;
	    }else if(methodStart > start && methodStart < end){
	        return true;
	    }else if(methodStart <= start && methodEnd >= end){
	        return true;
	    }
	    return false;
	}

	public void visit(MethodDeclaration md, Object arg){
//	    System.out.println("mdstart:"+md.getBeginLine());
//        System.out.println("mdend:"+md.getEndLine());
//        System.out.println("start:"+start);
//        System.out.println("end:"+end);

		if(this.m == null && this.hasInterception(md.getBeginLine(), md.getEndLine())){
			this.m = new Method(md.getName(), md.getType().toString());
			List<String> paraNames = new ArrayList<String>();
			if(md.getParameters()!=null){
			    for(Parameter p : md.getParameters()){
			        paraNames.add(p.getId().getName());
			    }
			}
			this.m.setParameters(paraNames);
		}
	}
   public void visit(ConstructorDeclaration md, Object arg){
        if(this.m == null && this.hasInterception(md.getBeginLine(), md.getEndLine())){
            this.m = new Method(md.getName(), md.getName());
            List<String> paraNames = new ArrayList<String>();
            if(md.getParameters()!=null){
                for(Parameter p : md.getParameters()){
                    paraNames.add(p.getId().getName());
                }
            }
            this.m.setParameters(paraNames);
        }
    }


	public Method getMethod() {
		// TODO Auto-generated method stub
		return m;
	}
}
