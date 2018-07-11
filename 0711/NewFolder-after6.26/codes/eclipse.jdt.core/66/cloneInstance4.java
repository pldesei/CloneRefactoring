				for (int i = 0; i < genericArgumentsCounter; i++) {
					this.contents[localContentsOffset++] = 0;
					this.contents[localContentsOffset++] = 0;
					this.contents[localContentsOffset++] = (byte) (code_length >> 8);
					this.contents[localContentsOffset++] = (byte) code_length;
					nameIndex = genericArgumentsNameIndexes[i];
					this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
					this.contents[localContentsOffset++] = (byte) nameIndex;
					descriptorIndex = this.constantPool.literalIndex(genericArgumentsTypeBindings[i].genericTypeSignature());
					this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
					this.contents[localContentsOffset++] = (byte) descriptorIndex;
					int resolvedPosition = genericArgumentsResolvedPositions[i];
					this.contents[localContentsOffset++] = (byte) (resolvedPosition >> 8);
					this.contents[localContentsOffset++] = (byte) resolvedPosition;
				}
