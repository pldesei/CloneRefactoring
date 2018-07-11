		for (int i = 0; i < 4; i++) {
			try {
				poolContent[currentOffset++] = (byte) (key >>> (24 - i * 8));
			} catch (IndexOutOfBoundsException e) { //currentOffset has been ++ already (see the -1)
				int length = poolContent.length;
				System.arraycopy(poolContent, 0, (poolContent = new byte[length * 2 + CONSTANTPOOL_INITIAL_SIZE]), 0, length);
				poolContent[currentOffset - 1] = (byte) (key >>> (24 - i * 8));
			}
		}
