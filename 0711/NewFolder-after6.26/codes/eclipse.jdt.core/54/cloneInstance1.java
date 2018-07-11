	private char[][] findUnresolvedReferenceAfter(int from, BlockScope scope, final char[][] discouragedNames) {
		final int discouragedNamesCount = discouragedNames == null ? 0 : discouragedNames.length;
		final ArrayList proposedNames = new ArrayList();
		
		UnresolvedReferenceNameFinder.UnresolvedReferenceNameRequestor nameRequestor = 
			new UnresolvedReferenceNameFinder.UnresolvedReferenceNameRequestor() {
				public void acceptName(char[] name) {
					for (int i = 0; i < discouragedNamesCount; i++) {
						if (CharOperation.equals(discouragedNames[i], name, false)) return;
					}
					
					int relevance = computeBaseRelevance();
					relevance += computeRelevanceForResolution(false);
					relevance += computeRelevanceForInterestingProposal();
					relevance += computeRelevanceForCaseMatching(completionToken, name);
					relevance += computeRelevanceForQualification(false);
					relevance += computeRelevanceForRestrictions(IAccessRule.K_ACCESSIBLE); // no access restriction for local variable
					CompletionEngine.this.noProposal = false;
					if(!CompletionEngine.this.requestor.isIgnored(CompletionProposal.LOCAL_VARIABLE_REF)) {
						CompletionProposal proposal = CompletionEngine.this.createProposal(CompletionProposal.LOCAL_VARIABLE_REF, CompletionEngine.this.actualCompletionPosition);
						proposal.setSignature(
							createTypeSignature(
									CharOperation.concatWith(JAVA_LANG, '.'),
									OBJECT));
						proposal.setPackageName(CharOperation.concatWith(JAVA_LANG, '.'));
						proposal.setTypeName(OBJECT);
						proposal.setName(name);
						proposal.setCompletion(name);
						proposal.setFlags(Flags.AccDefault);
						proposal.setReplaceRange(CompletionEngine.this.startPosition - CompletionEngine.this.offset, CompletionEngine.this.endPosition - CompletionEngine.this.offset);
						proposal.setRelevance(relevance);
						CompletionEngine.this.requestor.accept(proposal);
						if(DEBUG) {
							CompletionEngine.this.printDebug(proposal);
						}
					}
					
					proposedNames.add(name);
				}
			};
		
		ReferenceContext referenceContext = scope.referenceContext();
		if (referenceContext instanceof AbstractMethodDeclaration) {
			AbstractMethodDeclaration md = (AbstractMethodDeclaration)referenceContext;
			
			UnresolvedReferenceNameFinder nameFinder = new UnresolvedReferenceNameFinder(this);
			nameFinder.findAfter(
					completionToken,
					md.scope,
					md.scope.classScope(),
					from,
					md.bodyEnd,
					nameRequestor);
		} else if (referenceContext instanceof TypeDeclaration) {
			TypeDeclaration typeDeclaration = (TypeDeclaration) referenceContext;
			FieldDeclaration[] fields = typeDeclaration.fields;
			if (fields != null) {
				done : for (int i = 0; i < fields.length; i++) {
					if (fields[i] instanceof Initializer) {
						Initializer initializer = (Initializer) fields[i];
						if (initializer.block.sourceStart <= from &&
								from < initializer.bodyEnd) {
							UnresolvedReferenceNameFinder nameFinder = new UnresolvedReferenceNameFinder(this);
							nameFinder.findAfter(
										completionToken,
										typeDeclaration.scope,
										typeDeclaration.scope,
										from,
										initializer.bodyEnd,
										nameRequestor);
							break done;
						}
					}
				}
			}
		}
		
		int proposedNamesCount = proposedNames.size();
		if (proposedNamesCount > 0) {
			return (char[][])proposedNames.toArray(new char[proposedNamesCount][]);
		}
		
		return null;
	}
