This clone method is located in File: org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/parser/Parser.java
The line range of this clone method is: 12007-12032
The content of this clone method is as follows:
		public void endVisit(Initializer initializer, MethodScope scope) {
			if (initializer.block == null) return;
			TypeDeclaration[] foundTypes = null;
			int length = 0;
			if(this.typePtr > -1) {
				length = this.typePtr + 1;
				foundTypes = new TypeDeclaration[length];
				System.arraycopy(this.types, 0, foundTypes, 0, length);
			}
			ReferenceContext oldContext = Parser.this.referenceContext;
			Parser.this.recoveryScanner.resetTo(initializer.bodyStart, initializer.bodyEnd);
			Scanner oldScanner = Parser.this.scanner;
			Parser.this.scanner = Parser.this.recoveryScanner;
			parseStatements(
					this.enclosingType,
					initializer.bodyStart,
					initializer.bodyEnd,
					foundTypes,
					Parser.this.compilationUnit);
			Parser.this.scanner = oldScanner;
			Parser.this.referenceContext = oldContext;

			for (int i = 0; i < length; i++) {
				foundTypes[i].traverse(this.typeVisitor, scope);
			}
		}
