		if ((this.produceAttributes & ClassFileConstants.ATTR_VARS) != 0) {
			int numberOfEntries = 0;
			int localVariableNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
			if (localContentsOffset + 8 > this.contents.length) {
				resizeContents(8);
			}
			this.contents[localContentsOffset++] = (byte) (localVariableNameIndex >> 8);
			this.contents[localContentsOffset++] = (byte) localVariableNameIndex;
			int localVariableTableOffset = localContentsOffset;
			localContentsOffset += 6;
			// leave space for attribute_length and local_variable_table_length
			int nameIndex;
			int descriptorIndex;

			// used to remember the local variable with a generic type
			int genericLocalVariablesCounter = 0;
			LocalVariableBinding[] genericLocalVariables = null;
			int numberOfGenericEntries = 0;

			for (int i = 0, max = this.codeStream.allLocalsCounter; i < max; i++) {
				LocalVariableBinding localVariable = this.codeStream.locals[i];
				if (localVariable.declaration == null) continue;
				final TypeBinding localVariableTypeBinding = localVariable.type;
				boolean isParameterizedType = localVariableTypeBinding.isParameterizedType() || localVariableTypeBinding.isTypeVariable();
				if (localVariable.initializationCount != 0 && isParameterizedType) {
					if (genericLocalVariables == null) {
						// we cannot have more than max locals
						genericLocalVariables = new LocalVariableBinding[max];
					}
					genericLocalVariables[genericLocalVariablesCounter++] = localVariable;
				}
				for (int j = 0; j < localVariable.initializationCount; j++) {
					int startPC = localVariable.initializationPCs[j << 1];
					int endPC = localVariable.initializationPCs[(j << 1) + 1];
					if (startPC != endPC) { // only entries for non zero length
						if (endPC == -1) {
							localVariable.declaringScope.problemReporter().abortDueToInternalError(
								Messages.bind(Messages.abort_invalidAttribute, new String(localVariable.name)),
								(ASTNode) localVariable.declaringScope.methodScope().referenceContext);
						}
						if (localContentsOffset + 10 > this.contents.length) {
							resizeContents(10);
						}
						// now we can safely add the local entry
						numberOfEntries++;
						if (isParameterizedType) {
							numberOfGenericEntries++;
						}
						this.contents[localContentsOffset++] = (byte) (startPC >> 8);
						this.contents[localContentsOffset++] = (byte) startPC;
						int length = endPC - startPC;
						this.contents[localContentsOffset++] = (byte) (length >> 8);
						this.contents[localContentsOffset++] = (byte) length;
						nameIndex = this.constantPool.literalIndex(localVariable.name);
						this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
						this.contents[localContentsOffset++] = (byte) nameIndex;
						descriptorIndex = this.constantPool.literalIndex(localVariableTypeBinding.signature());
						this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
						this.contents[localContentsOffset++] = (byte) descriptorIndex;
						int resolvedPosition = localVariable.resolvedPosition;
						this.contents[localContentsOffset++] = (byte) (resolvedPosition >> 8);
						this.contents[localContentsOffset++] = (byte) resolvedPosition;
					}
				}
			}
			int value = numberOfEntries * 10 + 2;
			this.contents[localVariableTableOffset++] = (byte) (value >> 24);
			this.contents[localVariableTableOffset++] = (byte) (value >> 16);
			this.contents[localVariableTableOffset++] = (byte) (value >> 8);
			this.contents[localVariableTableOffset++] = (byte) value;
			this.contents[localVariableTableOffset++] = (byte) (numberOfEntries >> 8);
			this.contents[localVariableTableOffset] = (byte) numberOfEntries;
			attributeNumber++;

			if (genericLocalVariablesCounter != 0) {
				// add the local variable type table attribute
				int maxOfEntries = 8 + numberOfGenericEntries * 10;
				// reserve enough space
				if (localContentsOffset + maxOfEntries >= this.contents.length) {
					resizeContents(maxOfEntries);
				}
				int localVariableTypeNameIndex =
					this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTypeTableName);
				this.contents[localContentsOffset++] = (byte) (localVariableTypeNameIndex >> 8);
				this.contents[localContentsOffset++] = (byte) localVariableTypeNameIndex;
				value = numberOfGenericEntries * 10 + 2;
				this.contents[localContentsOffset++] = (byte) (value >> 24);
				this.contents[localContentsOffset++] = (byte) (value >> 16);
				this.contents[localContentsOffset++] = (byte) (value >> 8);
				this.contents[localContentsOffset++] = (byte) value;
				this.contents[localContentsOffset++] = (byte) (numberOfGenericEntries >> 8);
				this.contents[localContentsOffset++] = (byte) numberOfGenericEntries;

				for (int i = 0; i < genericLocalVariablesCounter; i++) {
					LocalVariableBinding localVariable = genericLocalVariables[i];
					for (int j = 0; j < localVariable.initializationCount; j++) {
						int startPC = localVariable.initializationPCs[j << 1];
						int endPC = localVariable.initializationPCs[(j << 1) + 1];
						if (startPC != endPC) { // only entries for non zero length
							// now we can safely add the local entry
							this.contents[localContentsOffset++] = (byte) (startPC >> 8);
							this.contents[localContentsOffset++] = (byte) startPC;
							int length = endPC - startPC;
							this.contents[localContentsOffset++] = (byte) (length >> 8);
							this.contents[localContentsOffset++] = (byte) length;
							nameIndex = this.constantPool.literalIndex(localVariable.name);
							this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
							this.contents[localContentsOffset++] = (byte) nameIndex;
							descriptorIndex = this.constantPool.literalIndex(localVariable.type.genericTypeSignature());
							this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
							this.contents[localContentsOffset++] = (byte) descriptorIndex;
							int resolvedPosition = localVariable.resolvedPosition;
							this.contents[localContentsOffset++] = (byte) (resolvedPosition >> 8);
							this.contents[localContentsOffset++] = (byte) resolvedPosition;
						}
					}
				}
				attributeNumber++;
			}
		}
