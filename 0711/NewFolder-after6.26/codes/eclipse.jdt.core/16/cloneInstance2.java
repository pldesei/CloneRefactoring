public int literalIndex(int key) {
	//Retrieve the index from the cache
	int index;
	// lazy initialization for base type caches
	// If it is null, initialize it, otherwise use it
	if (intCache == null) {
		intCache = new IntegerCache(INT_INITIAL_SIZE);
	}
	if ((index = intCache.get(key)) < 0) {
		index = intCache.put(key, currentIndex++);
		if (index > 0xFFFF){
			this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
		}
		// Write the integer constant entry into the constant pool
		// First add the tag
		writeU1(IntegerTag);
		// Then add the 4 bytes representing the int
		for (int i = 0; i < 4; i++) {
			try {
				poolContent[currentOffset++] = (byte) (key >>> (24 - i * 8));
			} catch (IndexOutOfBoundsException e) { //currentOffset has been ++ already (see the -1)
				int length = poolContent.length;
				System.arraycopy(poolContent, 0, (poolContent = new byte[length * 2 + CONSTANTPOOL_INITIAL_SIZE]), 0, length);
				poolContent[currentOffset - 1] = (byte) (key >>> (24 - i * 8));
			}
		}
	}
	return index;
}
