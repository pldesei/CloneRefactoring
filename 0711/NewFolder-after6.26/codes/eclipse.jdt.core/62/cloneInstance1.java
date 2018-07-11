		if ((this.produceAttributes & ClassFileConstants.ATTR_LINES) != 0) {
			/* Create and add the line number attribute (used for debugging)
			 * Build the pairs of:
			 * 	(bytecodePC lineNumber)
			 * according to the table of start line indexes and the pcToSourceMap table
			 * contained into the codestream
			 */
			int[] pcToSourceMapTable;
			if (((pcToSourceMapTable = this.codeStream.pcToSourceMap) != null)
				&& (this.codeStream.pcToSourceMapSize != 0)) {
				int lineNumberNameIndex =
					this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
				if (localContentsOffset + 8 >= this.contents.length) {
					resizeContents(8);
				}
				this.contents[localContentsOffset++] = (byte) (lineNumberNameIndex >> 8);
				this.contents[localContentsOffset++] = (byte) lineNumberNameIndex;
				int lineNumberTableOffset = localContentsOffset;
				localContentsOffset += 6;
				// leave space for attribute_length and line_number_table_length
				int numberOfEntries = 0;
				int length = this.codeStream.pcToSourceMapSize;
				for (int i = 0; i < length;) {
					// write the entry
					if (localContentsOffset + 4 >= this.contents.length) {
						resizeContents(4);
					}
					int pc = pcToSourceMapTable[i++];
					this.contents[localContentsOffset++] = (byte) (pc >> 8);
					this.contents[localContentsOffset++] = (byte) pc;
					int lineNumber = pcToSourceMapTable[i++];
					this.contents[localContentsOffset++] = (byte) (lineNumber >> 8);
					this.contents[localContentsOffset++] = (byte) lineNumber;
					numberOfEntries++;
				}
				// now we change the size of the line number attribute
				int lineNumberAttr_length = numberOfEntries * 4 + 2;
				this.contents[lineNumberTableOffset++] = (byte) (lineNumberAttr_length >> 24);
				this.contents[lineNumberTableOffset++] = (byte) (lineNumberAttr_length >> 16);
				this.contents[lineNumberTableOffset++] = (byte) (lineNumberAttr_length >> 8);
				this.contents[lineNumberTableOffset++] = (byte) lineNumberAttr_length;
				this.contents[lineNumberTableOffset++] = (byte) (numberOfEntries >> 8);
				this.contents[lineNumberTableOffset++] = (byte) numberOfEntries;
				attributeNumber++;
			}
		}
