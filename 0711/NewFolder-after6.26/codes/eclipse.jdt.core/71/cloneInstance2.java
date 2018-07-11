	public void completeCodeAttributeForClinit(int codeAttributeOffset) {
		// reinitialize the contents with the byte modified by the code stream
		this.contents = this.codeStream.bCodeStream;
		int localContentsOffset = this.codeStream.classFileOffset;
		// codeAttributeOffset is the position inside contents byte array before we started to write
		// any information about the codeAttribute
		// That means that to write the attribute_length you need to offset by 2 the value of codeAttributeOffset
		// to get the right position, 6 for the max_stack etc...
		int code_length = this.codeStream.position;
		if (code_length > 65535) {
			this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(
				this.codeStream.methodDeclaration.scope.referenceType());
		}
		if (localContentsOffset + 20 >= this.contents.length) {
			resizeContents(20);
		}
		int max_stack = this.codeStream.stackMax;
		this.contents[codeAttributeOffset + 6] = (byte) (max_stack >> 8);
		this.contents[codeAttributeOffset + 7] = (byte) max_stack;
		int max_locals = this.codeStream.maxLocals;
		this.contents[codeAttributeOffset + 8] = (byte) (max_locals >> 8);
		this.contents[codeAttributeOffset + 9] = (byte) max_locals;
		this.contents[codeAttributeOffset + 10] = (byte) (code_length >> 24);
		this.contents[codeAttributeOffset + 11] = (byte) (code_length >> 16);
		this.contents[codeAttributeOffset + 12] = (byte) (code_length >> 8);
		this.contents[codeAttributeOffset + 13] = (byte) code_length;

		boolean addStackMaps = (this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP_TABLE) != 0;
		// write the exception table
		ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
		int exceptionHandlersCount = 0; // each label holds one handler per range (start/end contiguous)
		for (int i = 0, length = this.codeStream.exceptionLabelsCounter; i < length; i++) {
			exceptionHandlersCount += this.codeStream.exceptionLabels[i].count / 2;
		}
		int exSize = exceptionHandlersCount * 8 + 2;
		if (exSize + localContentsOffset >= this.contents.length) {
			resizeContents(exSize);
		}
		// there is no exception table, so we need to offset by 2 the current offset and move
		// on the attribute generation
		this.contents[localContentsOffset++] = (byte) (exceptionHandlersCount >> 8);
		this.contents[localContentsOffset++] = (byte) exceptionHandlersCount;
		for (int i = 0, max = this.codeStream.exceptionLabelsCounter; i < max; i++) {
			ExceptionLabel exceptionLabel = exceptionLabels[i];
			if (exceptionLabel != null) {
				int iRange = 0, maxRange = exceptionLabel.count;
				if ((maxRange & 1) != 0) {
					this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(
							Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.methodDeclaration.selector)),
							this.codeStream.methodDeclaration);
				}
				while  (iRange < maxRange) {
					int start = exceptionLabel.ranges[iRange++]; // even ranges are start positions
					this.contents[localContentsOffset++] = (byte) (start >> 8);
					this.contents[localContentsOffset++] = (byte) start;
					int end = exceptionLabel.ranges[iRange++]; // odd ranges are end positions
					this.contents[localContentsOffset++] = (byte) (end >> 8);
					this.contents[localContentsOffset++] = (byte) end;
					int handlerPC = exceptionLabel.position;
					this.contents[localContentsOffset++] = (byte) (handlerPC >> 8);
					this.contents[localContentsOffset++] = (byte) handlerPC;
					if (addStackMaps) {
						StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream) this.codeStream;
						stackMapFrameCodeStream.addFramePosition(handlerPC);
//						stackMapFrameCodeStream.addExceptionMarker(handlerPC, exceptionLabel.exceptionType);
					}
					if (exceptionLabel.exceptionType == null) {
						// any exception handler
						this.contents[localContentsOffset++] = 0;
						this.contents[localContentsOffset++] = 0;
					} else {
						int nameIndex;
						if (exceptionLabel.exceptionType == TypeBinding.NULL) {
							/* represents denote ClassNotFoundException, see class literal access*/
							nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
						} else {
							nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
						}
						this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
						this.contents[localContentsOffset++] = (byte) nameIndex;
					}
				}
			}
		}
		// debug attributes
		int codeAttributeAttributeOffset = localContentsOffset;
		int attributeNumber = 0;
		// leave two bytes for the attribute_length
		localContentsOffset += 2;
		if (localContentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}

		// first we handle the linenumber attribute
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
		// then we do the local variable attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_VARS) != 0) {
			int numberOfEntries = 0;
			//		codeAttribute.addLocalVariableTableAttribute(this);
			if ((this.codeStream.pcToSourceMap != null)
				&& (this.codeStream.pcToSourceMapSize != 0)) {
				int localVariableNameIndex =
					this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
				if (localContentsOffset + 8 >= this.contents.length) {
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
							if (localContentsOffset + 10 >= this.contents.length) {
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
					// reserve enough space
					int maxOfEntries = 8 + numberOfGenericEntries * 10;

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
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP_TABLE) != 0) {
			StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream) this.codeStream;
			stackMapFrameCodeStream.removeFramePosition(code_length);
			if (stackMapFrameCodeStream.hasFramePositions()) {
				ArrayList frames = new ArrayList();
				traverse(null, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, true);
				int numberOfFrames = frames.size();
				if (numberOfFrames > 1) {
					int stackMapTableAttributeOffset = localContentsOffset;
					// add the stack map table attribute
					if (localContentsOffset + 8 >= this.contents.length) {
						resizeContents(8);
					}
					int stackMapTableAttributeNameIndex =
						this.constantPool.literalIndex(AttributeNamesConstants.StackMapTableName);
					this.contents[localContentsOffset++] = (byte) (stackMapTableAttributeNameIndex >> 8);
					this.contents[localContentsOffset++] = (byte) stackMapTableAttributeNameIndex;

					int stackMapTableAttributeLengthOffset = localContentsOffset;
					// generate the attribute
					localContentsOffset += 4;
					if (localContentsOffset + 4 >= this.contents.length) {
						resizeContents(4);
					}
					int numberOfFramesOffset = localContentsOffset;
					localContentsOffset += 2;
					if (localContentsOffset + 2 >= this.contents.length) {
						resizeContents(2);
					}
					StackMapFrame currentFrame = (StackMapFrame) frames.get(0);
					StackMapFrame prevFrame = null;
					for (int j = 1; j < numberOfFrames; j++) {
						// select next frame
						prevFrame = currentFrame;
						currentFrame = (StackMapFrame) frames.get(j);
						// generate current frame
						// need to find differences between the current frame and the previous frame
						int offsetDelta = currentFrame.getOffsetDelta(prevFrame);
						switch (currentFrame.getFrameType(prevFrame)) {
							case StackMapFrame.APPEND_FRAME :
								if (localContentsOffset + 3 >= this.contents.length) {
									resizeContents(3);
								}
								int numberOfDifferentLocals = currentFrame.numberOfDifferentLocals(prevFrame);
								this.contents[localContentsOffset++] = (byte) (251 + numberOfDifferentLocals);
								this.contents[localContentsOffset++] = (byte) (offsetDelta >> 8);
								this.contents[localContentsOffset++] = (byte) offsetDelta;
								int index = currentFrame.getIndexOfDifferentLocals(numberOfDifferentLocals);
								int numberOfLocals = currentFrame.getNumberOfLocals();
								for (int i = index; i < currentFrame.locals.length && numberOfDifferentLocals > 0; i++) {
									if (localContentsOffset + 6 >= this.contents.length) {
										resizeContents(6);
									}
									VerificationTypeInfo info = currentFrame.locals[i];
									if (info == null) {
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
									} else {
										switch(info.id()) {
											case T_boolean :
											case T_byte :
											case T_char :
											case T_int :
											case T_short :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
												break;
											case T_float :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
												break;
											case T_long :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
												i++;
												break;
											case T_double :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
												i++;
												break;
											case T_null :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
												break;
											default:
												this.contents[localContentsOffset++] = (byte) info.tag;
												switch (info.tag) {
													case VerificationTypeInfo.ITEM_UNINITIALIZED :
														int offset = info.offset;
														this.contents[localContentsOffset++] = (byte) (offset >> 8);
														this.contents[localContentsOffset++] = (byte) offset;
														break;
													case VerificationTypeInfo.ITEM_OBJECT :
														int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
														this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
														this.contents[localContentsOffset++] = (byte) indexForType;
												}
										}
										numberOfDifferentLocals--;
									}
								}
								break;
							case StackMapFrame.SAME_FRAME :
								if (localContentsOffset + 1 >= this.contents.length) {
									resizeContents(1);
								}
								this.contents[localContentsOffset++] = (byte) offsetDelta;
								break;
							case StackMapFrame.SAME_FRAME_EXTENDED :
								if (localContentsOffset + 3 >= this.contents.length) {
									resizeContents(3);
								}
								this.contents[localContentsOffset++] = (byte) 251;
								this.contents[localContentsOffset++] = (byte) (offsetDelta >> 8);
								this.contents[localContentsOffset++] = (byte) offsetDelta;
								break;
							case StackMapFrame.CHOP_FRAME :
								if (localContentsOffset + 3 >= this.contents.length) {
									resizeContents(3);
								}
								numberOfDifferentLocals = -currentFrame.numberOfDifferentLocals(prevFrame);
								this.contents[localContentsOffset++] = (byte) (251 - numberOfDifferentLocals);
								this.contents[localContentsOffset++] = (byte) (offsetDelta >> 8);
								this.contents[localContentsOffset++] = (byte) offsetDelta;
								break;
							case StackMapFrame.SAME_LOCALS_1_STACK_ITEMS :
								if (localContentsOffset + 4 >= this.contents.length) {
									resizeContents(4);
								}
								this.contents[localContentsOffset++] = (byte) (offsetDelta + 64);
								if (currentFrame.stackItems[0] == null) {
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
								} else {
									switch(currentFrame.stackItems[0].id()) {
										case T_boolean :
										case T_byte :
										case T_char :
										case T_int :
										case T_short :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
											break;
										case T_float :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
											break;
										case T_long :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
											break;
										case T_double :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
											break;
										case T_null :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
											break;
										default:
											VerificationTypeInfo info = currentFrame.stackItems[0];
											byte tag = (byte) info.tag;
											this.contents[localContentsOffset++] = tag;
											switch (tag) {
												case VerificationTypeInfo.ITEM_UNINITIALIZED :
													int offset = info.offset;
													this.contents[localContentsOffset++] = (byte) (offset >> 8);
													this.contents[localContentsOffset++] = (byte) offset;
													break;
												case VerificationTypeInfo.ITEM_OBJECT :
													int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
													this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
													this.contents[localContentsOffset++] = (byte) indexForType;
											}
									}
								}
								break;
							case StackMapFrame.SAME_LOCALS_1_STACK_ITEMS_EXTENDED :
								if (localContentsOffset + 6 >= this.contents.length) {
									resizeContents(6);
								}
								this.contents[localContentsOffset++] = (byte) 247;
								this.contents[localContentsOffset++] = (byte) (offsetDelta >> 8);
								this.contents[localContentsOffset++] = (byte) offsetDelta;
								if (currentFrame.stackItems[0] == null) {
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
								} else {
									switch(currentFrame.stackItems[0].id()) {
										case T_boolean :
										case T_byte :
										case T_char :
										case T_int :
										case T_short :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
											break;
										case T_float :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
											break;
										case T_long :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
											break;
										case T_double :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
											break;
										case T_null :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
											break;
										default:
											VerificationTypeInfo info = currentFrame.stackItems[0];
											byte tag = (byte) info.tag;
											this.contents[localContentsOffset++] = tag;
											switch (tag) {
												case VerificationTypeInfo.ITEM_UNINITIALIZED :
													int offset = info.offset;
													this.contents[localContentsOffset++] = (byte) (offset >> 8);
													this.contents[localContentsOffset++] = (byte) offset;
													break;
												case VerificationTypeInfo.ITEM_OBJECT :
													int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
													this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
													this.contents[localContentsOffset++] = (byte) indexForType;
											}
									}
								}
								break;
							default :
								// FULL_FRAME
								if (localContentsOffset + 5 >= this.contents.length) {
									resizeContents(5);
								}
								this.contents[localContentsOffset++] = (byte) 255;
								this.contents[localContentsOffset++] = (byte) (offsetDelta >> 8);
								this.contents[localContentsOffset++] = (byte) offsetDelta;
								int numberOfLocalOffset = localContentsOffset;
								localContentsOffset += 2; // leave two spots for number of locals
								int numberOfLocalEntries = 0;
								numberOfLocals = currentFrame.getNumberOfLocals();
								int numberOfEntries = 0;
								int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
								for (int i = 0; i < localsLength && numberOfLocalEntries < numberOfLocals; i++) {
									if (localContentsOffset + 3 >= this.contents.length) {
										resizeContents(3);
									}
									VerificationTypeInfo info = currentFrame.locals[i];
									if (info == null) {
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
									} else {
										switch(info.id()) {
											case T_boolean :
											case T_byte :
											case T_char :
											case T_int :
											case T_short :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
												break;
											case T_float :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
												break;
											case T_long :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
												i++;
												break;
											case T_double :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
												i++;
												break;
											case T_null :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
												break;
											default:
												this.contents[localContentsOffset++] = (byte) info.tag;
												switch (info.tag) {
													case VerificationTypeInfo.ITEM_UNINITIALIZED :
														int offset = info.offset;
														this.contents[localContentsOffset++] = (byte) (offset >> 8);
														this.contents[localContentsOffset++] = (byte) offset;
														break;
													case VerificationTypeInfo.ITEM_OBJECT :
														int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
														this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
														this.contents[localContentsOffset++] = (byte) indexForType;
												}
										}
										numberOfLocalEntries++;
									}
									numberOfEntries++;
								}
								if (localContentsOffset + 4 >= this.contents.length) {
									resizeContents(4);
								}
								this.contents[numberOfLocalOffset++] = (byte) (numberOfEntries >> 8);
								this.contents[numberOfLocalOffset] = (byte) numberOfEntries;
								int numberOfStackItems = currentFrame.numberOfStackItems;
								this.contents[localContentsOffset++] = (byte) (numberOfStackItems >> 8);
								this.contents[localContentsOffset++] = (byte) numberOfStackItems;
								for (int i = 0; i < numberOfStackItems; i++) {
									if (localContentsOffset + 3 >= this.contents.length) {
										resizeContents(3);
									}
									VerificationTypeInfo info = currentFrame.stackItems[i];
									if (info == null) {
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
									} else {
										switch(info.id()) {
											case T_boolean :
											case T_byte :
											case T_char :
											case T_int :
											case T_short :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
												break;
											case T_float :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
												break;
											case T_long :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
												break;
											case T_double :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
												break;
											case T_null :
												this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
												break;
											default:
												this.contents[localContentsOffset++] = (byte) info.tag;
												switch (info.tag) {
													case VerificationTypeInfo.ITEM_UNINITIALIZED :
														int offset = info.offset;
														this.contents[localContentsOffset++] = (byte) (offset >> 8);
														this.contents[localContentsOffset++] = (byte) offset;
														break;
													case VerificationTypeInfo.ITEM_OBJECT :
														int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
														this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
														this.contents[localContentsOffset++] = (byte) indexForType;
												}
										}
									}
								}
						}
					}

					numberOfFrames--;
					if (numberOfFrames != 0) {
						this.contents[numberOfFramesOffset++] = (byte) (numberOfFrames >> 8);
						this.contents[numberOfFramesOffset] = (byte) numberOfFrames;

						int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
						this.contents[stackMapTableAttributeLengthOffset++] = (byte) (attributeLength >> 24);
						this.contents[stackMapTableAttributeLengthOffset++] = (byte) (attributeLength >> 16);
						this.contents[stackMapTableAttributeLengthOffset++] = (byte) (attributeLength >> 8);
						this.contents[stackMapTableAttributeLengthOffset] = (byte) attributeLength;
						attributeNumber++;
					} else {
						localContentsOffset = stackMapTableAttributeOffset;
					}
				}
			}
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP) != 0) {
			StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream) this.codeStream;
			stackMapFrameCodeStream.removeFramePosition(code_length);
			if (stackMapFrameCodeStream.hasFramePositions()) {
				ArrayList frames = new ArrayList();
				traverse(this.codeStream.methodDeclaration.binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
				int numberOfFrames = frames.size();
				if (numberOfFrames > 1) {
					int stackMapTableAttributeOffset = localContentsOffset;
					// add the stack map table attribute
					if (localContentsOffset + 8 >= this.contents.length) {
						resizeContents(8);
					}
					int stackMapAttributeNameIndex =
						this.constantPool.literalIndex(AttributeNamesConstants.StackMapName);
					this.contents[localContentsOffset++] = (byte) (stackMapAttributeNameIndex >> 8);
					this.contents[localContentsOffset++] = (byte) stackMapAttributeNameIndex;

					int stackMapAttributeLengthOffset = localContentsOffset;
					// generate the attribute
					localContentsOffset += 4;
					if (localContentsOffset + 4 >= this.contents.length) {
						resizeContents(4);
					}
					int numberOfFramesOffset = localContentsOffset;
					localContentsOffset += 2;
					if (localContentsOffset + 2 >= this.contents.length) {
						resizeContents(2);
					}
					StackMapFrame currentFrame = (StackMapFrame) frames.get(0);
					for (int j = 1; j < numberOfFrames; j++) {
						// select next frame
						currentFrame = (StackMapFrame) frames.get(j);
						// generate current frame
						// need to find differences between the current frame and the previous frame
						int frameOffset = currentFrame.pc;
						// FULL_FRAME
						if (localContentsOffset + 5 >= this.contents.length) {
							resizeContents(5);
						}
						this.contents[localContentsOffset++] = (byte) (frameOffset >> 8);
						this.contents[localContentsOffset++] = (byte) frameOffset;
						int numberOfLocalOffset = localContentsOffset;
						localContentsOffset += 2; // leave two spots for number of locals
						int numberOfLocalEntries = 0;
						int numberOfLocals = currentFrame.getNumberOfLocals();
						int numberOfEntries = 0;
						int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
						for (int i = 0; i < localsLength && numberOfLocalEntries < numberOfLocals; i++) {
							if (localContentsOffset + 3 >= this.contents.length) {
								resizeContents(3);
							}
							VerificationTypeInfo info = currentFrame.locals[i];
							if (info == null) {
								this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
							} else {
								switch(info.id()) {
									case T_boolean :
									case T_byte :
									case T_char :
									case T_int :
									case T_short :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
										break;
									case T_float :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
										break;
									case T_long :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
										i++;
										break;
									case T_double :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
										i++;
										break;
									case T_null :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
										break;
									default:
										this.contents[localContentsOffset++] = (byte) info.tag;
									switch (info.tag) {
										case VerificationTypeInfo.ITEM_UNINITIALIZED :
											int offset = info.offset;
											this.contents[localContentsOffset++] = (byte) (offset >> 8);
											this.contents[localContentsOffset++] = (byte) offset;
											break;
										case VerificationTypeInfo.ITEM_OBJECT :
											int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
											this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
											this.contents[localContentsOffset++] = (byte) indexForType;
									}
								}
								numberOfLocalEntries++;
							}
							numberOfEntries++;
						}
						if (localContentsOffset + 4 >= this.contents.length) {
							resizeContents(4);
						}
						this.contents[numberOfLocalOffset++] = (byte) (numberOfEntries >> 8);
						this.contents[numberOfLocalOffset] = (byte) numberOfEntries;
						int numberOfStackItems = currentFrame.numberOfStackItems;
						this.contents[localContentsOffset++] = (byte) (numberOfStackItems >> 8);
						this.contents[localContentsOffset++] = (byte) numberOfStackItems;
						for (int i = 0; i < numberOfStackItems; i++) {
							if (localContentsOffset + 3 >= this.contents.length) {
								resizeContents(3);
							}
							VerificationTypeInfo info = currentFrame.stackItems[i];
							if (info == null) {
								this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
							} else {
								switch(info.id()) {
									case T_boolean :
									case T_byte :
									case T_char :
									case T_int :
									case T_short :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
										break;
									case T_float :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
										break;
									case T_long :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
										break;
									case T_double :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
										break;
									case T_null :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
										break;
									default:
										this.contents[localContentsOffset++] = (byte) info.tag;
									switch (info.tag) {
										case VerificationTypeInfo.ITEM_UNINITIALIZED :
											int offset = info.offset;
											this.contents[localContentsOffset++] = (byte) (offset >> 8);
											this.contents[localContentsOffset++] = (byte) offset;
											break;
										case VerificationTypeInfo.ITEM_OBJECT :
											int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
											this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
											this.contents[localContentsOffset++] = (byte) indexForType;
									}
								}
							}
						}
					}

					numberOfFrames--;
					if (numberOfFrames != 0) {
						this.contents[numberOfFramesOffset++] = (byte) (numberOfFrames >> 8);
						this.contents[numberOfFramesOffset] = (byte) numberOfFrames;

						int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
						this.contents[stackMapAttributeLengthOffset++] = (byte) (attributeLength >> 24);
						this.contents[stackMapAttributeLengthOffset++] = (byte) (attributeLength >> 16);
						this.contents[stackMapAttributeLengthOffset++] = (byte) (attributeLength >> 8);
						this.contents[stackMapAttributeLengthOffset] = (byte) attributeLength;
						attributeNumber++;
					} else {
						localContentsOffset = stackMapTableAttributeOffset;
					}
				}
			}
		}

		// update the number of attributes
		// ensure first that there is enough space available inside the contents array
		if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}
		this.contents[codeAttributeAttributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[codeAttributeAttributeOffset] = (byte) attributeNumber;
		// update the attribute length
		int codeAttributeLength = localContentsOffset - (codeAttributeOffset + 6);
		this.contents[codeAttributeOffset + 2] = (byte) (codeAttributeLength >> 24);
		this.contents[codeAttributeOffset + 3] = (byte) (codeAttributeLength >> 16);
		this.contents[codeAttributeOffset + 4] = (byte) (codeAttributeLength >> 8);
		this.contents[codeAttributeOffset + 5] = (byte) codeAttributeLength;
		this.contentsOffset = localContentsOffset;
	}
