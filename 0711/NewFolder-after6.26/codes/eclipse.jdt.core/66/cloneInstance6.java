				for (int i = 0; i < genericLocalVariablesCounter; i++) {
					LocalVariableBinding localVariable = genericLocalVariables[i];
					for (int j = 0; j < localVariable.initializationCount; j++) {
						int startPC = localVariable.initializationPCs[j << 1];
						int endPC = localVariable.initializationPCs[(j << 1) + 1];
						if (startPC != endPC) { // only entries for non zero length
							// now we can safely add the local entry
							this.contents[localContentsOffset++] = (byte) (startPC >> 8);
							this.contents[localContentsOffset++] = (byte) startPC;
							int length = endPC - startPC;
							this.contents[localContentsOffset++] = (byte) (length >> 8);
							this.contents[localContentsOffset++] = (byte) length;
							nameIndex = this.constantPool.literalIndex(localVariable.name);
							this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
							this.contents[localContentsOffset++] = (byte) nameIndex;
							descriptorIndex = this.constantPool.literalIndex(localVariable.type.genericTypeSignature());
							this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
							this.contents[localContentsOffset++] = (byte) descriptorIndex;
							int resolvedPosition = localVariable.resolvedPosition;
							this.contents[localContentsOffset++] = (byte) (resolvedPosition >> 8);
							this.contents[localContentsOffset++] = (byte) resolvedPosition;
						}
					}
				}
