public int literalIndex(float key) {
	//Retrieve the index from the cache
	int index;
	// lazy initialization for base type caches
	// If it is null, initialize it, otherwise use it
	if (floatCache == null) {
		floatCache = new FloatCache(FLOAT_INITIAL_SIZE);
	}
	if ((index = floatCache.get(key)) < 0) {
		index = floatCache.put(key, currentIndex++);
		if (index > 0xFFFF){
			this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
		}
		// Write the float constant entry into the constant pool
		// First add the tag
		writeU1(FloatTag);
		// Then add the 4 bytes representing the float
		int temp = java.lang.Float.floatToIntBits(key);
		for (int i = 0; i < 4; i++) {
			try {
				poolContent[currentOffset++] = (byte) (temp >>> (24 - i * 8));
			} catch (IndexOutOfBoundsException e) { //currentOffset has been ++ already (see the -1)
				int length = poolContent.length;
				System.arraycopy(poolContent, 0, (poolContent = new byte[length * 2 + CONSTANTPOOL_INITIAL_SIZE]), 0, length);
				poolContent[currentOffset - 1] = (byte) (temp >>> (24 - i * 8));
			}
		}
	}
	return index;
}
