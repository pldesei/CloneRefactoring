		if (this.targetJDK < ClassFileConstants.JDK1_5 && methodBinding.isSynthetic()) {
			// Synthetic attribute
			// Check that there is enough space to write the deprecated attribute
			if (contentsOffset + 6 >= (contentsLength = contents.length)) {
				System.arraycopy(
					contents,
					0,
					(contents = new byte[contentsLength + INCREMENT_SIZE]),
					0,
					contentsLength);
			}
			int syntheticAttributeNameIndex =
				constantPool.literalIndex(AttributeNamesConstants.SyntheticName);
			contents[contentsOffset++] = (byte) (syntheticAttributeNameIndex >> 8);
			contents[contentsOffset++] = (byte) syntheticAttributeNameIndex;
			// the length of a synthetic attribute is equals to 0
			contents[contentsOffset++] = 0;
			contents[contentsOffset++] = 0;
			contents[contentsOffset++] = 0;
			contents[contentsOffset++] = 0;

			attributeNumber++;
		}
