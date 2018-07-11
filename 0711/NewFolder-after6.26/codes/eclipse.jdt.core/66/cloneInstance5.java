				for (int j = 0; j < localVariable.initializationCount; j++) {
					int startPC = localVariable.initializationPCs[j << 1];
					int endPC = localVariable.initializationPCs[(j << 1) + 1];
					if (startPC != endPC) { // only entries for non zero length
						if (endPC == -1) {
							localVariable.declaringScope.problemReporter().abortDueToInternalError(
								Messages.bind(Messages.abort_invalidAttribute, new String(localVariable.name)),
								(ASTNode) localVariable.declaringScope.methodScope().referenceContext);
						}
						if (localContentsOffset + 10 > this.contents.length) {
							resizeContents(10);
						}
						// now we can safely add the local entry
						numberOfEntries++;
						if (isParameterizedType) {
							numberOfGenericEntries++;
						}
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
