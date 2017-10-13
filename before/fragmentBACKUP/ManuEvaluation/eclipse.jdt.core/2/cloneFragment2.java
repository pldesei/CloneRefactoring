This clone fragment is located in File: org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/parser/Parser.java
The line range of this clone fragment is: 12036-12060
The content of this clone fragment is as follows:
		private void endVisitMethod(AbstractMethodDeclaration methodDeclaration, ClassScope scope) {
			TypeDeclaration[] foundTypes = null;
			int length = 0;
			if(this.typePtr > -1) {
				length = this.typePtr + 1;
				foundTypes = new TypeDeclaration[length];
				System.arraycopy(this.types, 0, foundTypes, 0, length);
			}
			ReferenceContext oldContext = Parser.this.referenceContext;
			Parser.this.recoveryScanner.resetTo(methodDeclaration.bodyStart, methodDeclaration.bodyEnd);
			Scanner oldScanner = Parser.this.scanner;
			Parser.this.scanner = Parser.this.recoveryScanner;
			parseStatements(
					methodDeclaration,
					methodDeclaration.bodyStart,
					methodDeclaration.bodyEnd,
					foundTypes,
					Parser.this.compilationUnit);
			Parser.this.scanner = oldScanner;
			Parser.this.referenceContext = oldContext;

			for (int i = 0; i < length; i++) {
				foundTypes[i].traverse(this.typeVisitor, scope);
			}
		}
