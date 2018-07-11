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
