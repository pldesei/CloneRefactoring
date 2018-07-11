		if (this.targetJDK < ClassFileConstants.JDK1_5 && fieldBinding.isSynthetic()) {
			if (this.contentsOffset + 6 >= this.contents.length) {
				resizeContents(6);
			}
			int syntheticAttributeNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.SyntheticName);
			this.contents[this.contentsOffset++] = (byte) (syntheticAttributeNameIndex >> 8);
			this.contents[this.contentsOffset++] = (byte) syntheticAttributeNameIndex;
			// the length of a synthetic attribute is equals to 0
			this.contents[this.contentsOffset++] = 0;
			this.contents[this.contentsOffset++] = 0;
			this.contents[this.contentsOffset++] = 0;
			this.contents[this.contentsOffset++] = 0;
			attributesNumber++;
		}
