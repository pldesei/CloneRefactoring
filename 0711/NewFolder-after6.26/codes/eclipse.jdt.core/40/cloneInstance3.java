	} else {
		char[][] tokens = new char[length][];
		this.identifierPtr -= length;
		long[] positions = new long[length];
		System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
		System.arraycopy(
			this.identifierPositionStack, 
			this.identifierPtr + 1, 
			positions, 
			0, 
			length);
		typeReference = new QualifiedTypeReference(tokens, positions);
	}
