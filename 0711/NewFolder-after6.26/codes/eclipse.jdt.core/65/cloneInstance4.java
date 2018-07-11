		if ((this.produceAttributes & ClassFileConstants.ATTR_LINES) != 0) {
			if (localContentsOffset + 12 >= this.contents.length) {
				resizeContents(12);
			}
			int index = 0;
			int lineNumberNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
			this.contents[localContentsOffset++] = (byte) (lineNumberNameIndex >> 8);
			this.contents[localContentsOffset++] = (byte) lineNumberNameIndex;
			int lineNumberTableOffset = localContentsOffset;
			localContentsOffset += 6;
			// leave space for attribute_length and line_number_table_length
			// Seems like do would be better, but this preserves the existing behavior.
			index = Util.getLineNumber(binding.sourceStart, startLineIndexes, 0, startLineIndexes.length-1);
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = (byte) (index >> 8);
			this.contents[localContentsOffset++] = (byte) index;
			// now we change the size of the line number attribute
			this.contents[lineNumberTableOffset++] = 0;
			this.contents[lineNumberTableOffset++] = 0;
			this.contents[lineNumberTableOffset++] = 0;
			this.contents[lineNumberTableOffset++] = 6;
			this.contents[lineNumberTableOffset++] = 0;
			this.contents[lineNumberTableOffset++] = 1;
			attributeNumber++;
		}
