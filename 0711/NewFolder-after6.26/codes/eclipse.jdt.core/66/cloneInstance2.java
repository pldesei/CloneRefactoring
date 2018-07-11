				if (currentInstanceIsGeneric) {
					numberOfEntries++;
					this.contents[localContentsOffset++] = 0; // the startPC for this is always 0
					this.contents[localContentsOffset++] = 0;
					this.contents[localContentsOffset++] = (byte) (code_length >> 8);
					this.contents[localContentsOffset++] = (byte) code_length;
					nameIndex = this.constantPool.literalIndex(ConstantPool.This);
					this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
					this.contents[localContentsOffset++] = (byte) nameIndex;
					descriptorIndex = this.constantPool.literalIndex(declaringClassBinding.genericTypeSignature());
					this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
					this.contents[localContentsOffset++] = (byte) descriptorIndex;
					this.contents[localContentsOffset++] = 0;// the resolved position for this is always 0
					this.contents[localContentsOffset++] = 0;
				}
