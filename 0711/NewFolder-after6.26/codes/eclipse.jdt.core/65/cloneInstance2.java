		if ((this.produceAttributes & ClassFileConstants.ATTR_LINES) != 0) {
			if (localContentsOffset + 12 >= this.contents.length) {
				resizeContents(12);
			}
			/* Create and add the line number attribute (used for debugging)
				* Build the pairs of:
				* (bytecodePC lineNumber)
				* according to the table of start line indexes and the pcToSourceMap table
				* contained into the codestream
				*/
			int lineNumberNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
			this.contents[localContentsOffset++] = (byte) (lineNumberNameIndex >> 8);
			this.contents[localContentsOffset++] = (byte) lineNumberNameIndex;
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 6;
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 1;
			if (problemLine == 0) {
				problemLine = Util.getLineNumber(binding.sourceStart(), startLineIndexes, 0, startLineIndexes.length-1);
			}
			// first entry at pc = 0
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = (byte) (problemLine >> 8);
			this.contents[localContentsOffset++] = (byte) problemLine;
			// now we change the size of the line number attribute
			attributeNumber++;
		}
