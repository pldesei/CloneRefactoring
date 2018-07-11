				for (int i = 0; i < genericLocalVariablesCounter; i++) {
					LocalVariableBinding localVariable = genericLocalVariables[i];
					this.contents[localContentsOffset++] = 0;
					this.contents[localContentsOffset++] = 0;
					this.contents[localContentsOffset++] = (byte) (code_length >> 8);
					this.contents[localContentsOffset++] = (byte) code_length;
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
