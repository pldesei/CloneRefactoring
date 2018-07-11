			if (!methodDeclarationIsStatic) {
				numberOfEntries++;
				if (localContentsOffset + 10 >= this.contents.length) {
					resizeContents(10);
				}
				this.contents[localContentsOffset++] = 0;
				this.contents[localContentsOffset++] = 0;
				this.contents[localContentsOffset++] = (byte) (code_length >> 8);
				this.contents[localContentsOffset++] = (byte) code_length;
				nameIndex = this.constantPool.literalIndex(ConstantPool.This);
				this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
				this.contents[localContentsOffset++] = (byte) nameIndex;
				declaringClassBinding = (SourceTypeBinding) this.codeStream.methodDeclaration.binding.declaringClass;
				descriptorIndex =
					this.constantPool.literalIndex(declaringClassBinding.signature());
				this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
				this.contents[localContentsOffset++] = (byte) descriptorIndex;
				// the resolved position for this is always 0
				this.contents[localContentsOffset++] = 0;
				this.contents[localContentsOffset++] = 0;
			}
