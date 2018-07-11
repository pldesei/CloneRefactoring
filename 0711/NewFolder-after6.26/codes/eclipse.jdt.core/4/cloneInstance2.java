			compilationUnitDeclaration.traverse(this, compilationUnitDeclaration.scope);
		} catch(AbortFormatting e){
			StringBuffer buffer = new StringBuffer(this.scribe.formattedSource());
			buffer.append(compilationUnitSource, this.scribe.scanner.getCurrentTokenEndPosition(), this.scribe.scannerEndPosition - this.scribe.scanner.getCurrentTokenEndPosition());
			if (DEBUG) {
				System.out.println("COULD NOT FORMAT \n" + this.scribe.scanner); //$NON-NLS-1$
				System.out.println(this.scribe);
			}
			return buffer.toString();
