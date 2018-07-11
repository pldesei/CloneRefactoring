		if (genericSignature != null) {
			// check that there is enough space to write all the bytes for the field info corresponding
			// to the @fieldBinding
			if (this.contentsOffset + 8 >= this.contents.length) {
				resizeContents(8);
			}
			int signatureAttributeNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.SignatureName);
			this.contents[this.contentsOffset++] = (byte) (signatureAttributeNameIndex >> 8);
			this.contents[this.contentsOffset++] = (byte) signatureAttributeNameIndex;
			// the length of a signature attribute is equals to 2
			this.contents[this.contentsOffset++] = 0;
			this.contents[this.contentsOffset++] = 0;
			this.contents[this.contentsOffset++] = 0;
			this.contents[this.contentsOffset++] = 2;
			int signatureIndex =
				this.constantPool.literalIndex(genericSignature);
			this.contents[this.contentsOffset++] = (byte) (signatureIndex >> 8);
			this.contents[this.contentsOffset++] = (byte) signatureIndex;
			attributeNumber++;
		}
