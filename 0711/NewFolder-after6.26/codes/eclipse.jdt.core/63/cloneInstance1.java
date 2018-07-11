			for (int i = 0, max = this.codeStream.allLocalsCounter; i < max; i++) {
				LocalVariableBinding localVariable = this.codeStream.locals[i];
				if (localVariable.declaration == null) continue;
				final TypeBinding localVariableTypeBinding = localVariable.type;
				boolean isParameterizedType = localVariableTypeBinding.isParameterizedType() || localVariableTypeBinding.isTypeVariable();
				if (localVariable.initializationCount != 0 && isParameterizedType) {
					if (genericLocalVariables == null) {
						// we cannot have more than max locals
						genericLocalVariables = new LocalVariableBinding[max];
					}
					genericLocalVariables[genericLocalVariablesCounter++] = localVariable;
				}
				for (int j = 0; j < localVariable.initializationCount; j++) {
					int startPC = localVariable.initializationPCs[j << 1];
					int endPC = localVariable.initializationPCs[(j << 1) + 1];
					if (startPC != endPC) { // only entries for non zero length
						if (endPC == -1) {
							localVariable.declaringScope.problemReporter().abortDueToInternalError(
									Messages.bind(Messages.abort_invalidAttribute, new String(localVariable.name)),
									(ASTNode) localVariable.declaringScope.methodScope().referenceContext);
						}
						if (isParameterizedType) {
							numberOfGenericEntries++;
						}
						// now we can safely add the local entry
						numberOfEntries++;
						this.contents[localContentsOffset++] = (byte) (startPC >> 8);
						this.contents[localContentsOffset++] = (byte) startPC;
						int length = endPC - startPC;
						this.contents[localContentsOffset++] = (byte) (length >> 8);
						this.contents[localContentsOffset++] = (byte) length;
						nameIndex = this.constantPool.literalIndex(localVariable.name);
						this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
						this.contents[localContentsOffset++] = (byte) nameIndex;
						descriptorIndex = this.constantPool.literalIndex(localVariableTypeBinding.signature());
						this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
						this.contents[localContentsOffset++] = (byte) descriptorIndex;
						int resolvedPosition = localVariable.resolvedPosition;
						this.contents[localContentsOffset++] = (byte) (resolvedPosition >> 8);
						this.contents[localContentsOffset++] = (byte) resolvedPosition;
					}
				}
			}
