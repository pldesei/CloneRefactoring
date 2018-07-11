			if (methodBinding.isVarargs()) {
				/*
				 * handle of the target jsr14 for varargs in the source
				 * Varargs attribute
				 * Check that there is enough space to write the deprecated attribute
				 */
				if (this.contentsOffset + 6 >= this.contents.length) {
					resizeContents(6);
				}
				int varargsAttributeNameIndex =
					this.constantPool.literalIndex(AttributeNamesConstants.VarargsName);
				this.contents[this.contentsOffset++] = (byte) (varargsAttributeNameIndex >> 8);
				this.contents[this.contentsOffset++] = (byte) varargsAttributeNameIndex;
				// the length of a varargs attribute is equals to 0
				this.contents[this.contentsOffset++] = 0;
				this.contents[this.contentsOffset++] = 0;
				this.contents[this.contentsOffset++] = 0;
				this.contents[this.contentsOffset++] = 0;

				attributeNumber++;
			}
