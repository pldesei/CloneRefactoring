	 */
	public String format(String string, int[] positions, ConstructorDeclaration constructorDeclaration) {
		// reset the scribe
		this.scribe.reset();
		
		long startTime = System.currentTimeMillis();

		final char[] compilationUnitSource = string.toCharArray();
		
		this.scribe.scanner.setSource(compilationUnitSource);
		this.localScanner.setSource(compilationUnitSource);
		this.scribe.scannerEndPosition = compilationUnitSource.length;
		this.scribe.scanner.resetTo(0, this.scribe.scannerEndPosition);
		if (constructorDeclaration == null) {
			return string;
		}

		this.lastLocalDeclarationSourceStart = 0;
		try {
			ExplicitConstructorCall explicitConstructorCall = constructorDeclaration.constructorCall;
			if (explicitConstructorCall != SuperReference.implicitSuperConstructorCall()) {
				explicitConstructorCall.traverse(this, null);
			}
			Statement[] statements = constructorDeclaration.statements;
			if (statements != null) {
				formatStatements(null, statements);
			}
		} catch(AbortFormatting e){
			StringBuffer buffer = new StringBuffer(this.scribe.formattedSource());
			buffer.append(compilationUnitSource, this.scribe.scanner.getCurrentTokenEndPosition(), this.scribe.scannerEndPosition - this.scribe.scanner.getCurrentTokenEndPosition());
			if (DEBUG) {
				System.out.println("COULD NOT FORMAT \n" + this.scribe.scanner); //$NON-NLS-1$
				System.out.println(this.scribe);
			}
			return buffer.toString();
		}
		if (DEBUG){
			System.out.println("Formatting time: " + (System.currentTimeMillis() - startTime));  //$NON-NLS-1$
		}
		return this.scribe.toString();
