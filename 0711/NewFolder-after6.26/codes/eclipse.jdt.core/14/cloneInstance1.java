		if (referenceBinding.isDeprecated()) {
			// check that there is enough space to write all the bytes for the field info corresponding
			// to the @fieldBinding
			if (contentsOffset + 6 >= (contentsLength = contents.length)) {
				System.arraycopy(
					contents,
					0,
					(contents = new byte[contentsLength + INCREMENT_SIZE]),
					0,
					contentsLength);
			}
			int deprecatedAttributeNameIndex =
				constantPool.literalIndex(AttributeNamesConstants.DeprecatedName);
			contents[contentsOffset++] = (byte) (deprecatedAttributeNameIndex >> 8);
			contents[contentsOffset++] = (byte) deprecatedAttributeNameIndex;
			// the length of a deprecated attribute is equals to 0
			contents[contentsOffset++] = 0;
			contents[contentsOffset++] = 0;
			contents[contentsOffset++] = 0;
			contents[contentsOffset++] = 0;
			attributeNumber++;
		}
