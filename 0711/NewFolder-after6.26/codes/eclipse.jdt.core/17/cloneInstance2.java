public int literalIndexForLdc(char[] stringCharArray) {
	int index;
	if ((index = stringCache.get(stringCharArray)) < 0) {
		int stringIndex;
		// The entry doesn't exit yet
		if ((stringIndex = UTF8Cache.get(stringCharArray)) < 0) {
			// The entry doesn't exit yet
			// Write the tag first
			writeU1(Utf8Tag);
			// Then the size of the stringName array
			int savedCurrentOffset = currentOffset;
			if (currentOffset + 2 >= poolContent.length) {
				// we need to resize the poolContent array because we won't have
				// enough space to write the length
				int length = poolContent.length;
				System.arraycopy(poolContent, 0, (poolContent = new byte[length + CONSTANTPOOL_GROW_SIZE]), 0, length);
			}
			currentOffset += 2;
			int length = 0;
			for (int i = 0; i < stringCharArray.length; i++) {
				char current = stringCharArray[i];
				if ((current >= 0x0001) && (current <= 0x007F)) {
					// we only need one byte: ASCII table
					writeU1(current);
					length++;
				} else
					if (current > 0x07FF) {
						// we need 3 bytes
						length += 3;
						writeU1(0xE0 | ((current >> 12) & 0x0F)); // 0xE0 = 1110 0000
						writeU1(0x80 | ((current >> 6) & 0x3F)); // 0x80 = 1000 0000
						writeU1(0x80 | (current & 0x3F)); // 0x80 = 1000 0000
					} else {
						// we can be 0 or between 0x0080 and 0x07FF
						// In that case we only need 2 bytes
						length += 2;
						writeU1(0xC0 | ((current >> 6) & 0x1F)); // 0xC0 = 1100 0000
						writeU1(0x80 | (current & 0x3F)); // 0x80 = 1000 0000
					}
			}
			if (length >= 65535) {
				currentOffset = savedCurrentOffset - 1;
				return -1;
			}
			stringIndex = UTF8Cache.put(stringCharArray, currentIndex++);
			// Now we know the length that we have to write in the constant pool
			// we use savedCurrentOffset to do that
			if (length > 65535) {
				return 0;
			}
			poolContent[savedCurrentOffset] = (byte) (length >> 8);
			poolContent[savedCurrentOffset + 1] = (byte) length;
		}
		index = stringCache.put(stringCharArray, currentIndex++);
		if (index > 0xFFFF){
			this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
		}
		// Write the tag first
		writeU1(StringTag);
		// Then the string index
		writeU2(stringIndex);
	}
	return index;
}
