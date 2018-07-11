	public void completeCodeAttributeForProblemMethod(AbstractMethodDeclaration method, MethodBinding binding, int codeAttributeOffset, int[] startLineIndexes, int problemLine) {
		// reinitialize the localContents with the byte modified by the code stream
		this.contents = this.codeStream.bCodeStream;
		int localContentsOffset = this.codeStream.classFileOffset;
		// codeAttributeOffset is the position inside localContents byte array before we started to write// any information about the codeAttribute// That means that to write the attribute_length you need to offset by 2 the value of codeAttributeOffset// to get the right position, 6 for the max_stack etc...
		int max_stack = this.codeStream.stackMax;
		this.contents[codeAttributeOffset + 6] = (byte) (max_stack >> 8);
		this.contents[codeAttributeOffset + 7] = (byte) max_stack;
		int max_locals = this.codeStream.maxLocals;
		this.contents[codeAttributeOffset + 8] = (byte) (max_locals >> 8);
		this.contents[codeAttributeOffset + 9] = (byte) max_locals;
		int code_length = this.codeStream.position;
		this.contents[codeAttributeOffset + 10] = (byte) (code_length >> 24);
		this.contents[codeAttributeOffset + 11] = (byte) (code_length >> 16);
		this.contents[codeAttributeOffset + 12] = (byte) (code_length >> 8);
		this.contents[codeAttributeOffset + 13] = (byte) code_length;
		// write the exception table
		if (localContentsOffset + 50 >= this.contents.length) {
			resizeContents(50);
		}

		// write the exception table
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		// debug attributes
		int codeAttributeAttributeOffset = localContentsOffset;
		int attributeNumber = 0; // leave two bytes for the attribute_length
		localContentsOffset += 2; // first we handle the linenumber attribute
		if (localContentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_LINES) != 0) {
			if (localContentsOffset + 20 >= this.contents.length) {
				resizeContents(20);
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
		// then we do the local variable attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_VARS) != 0) {
			// compute the resolved position for the arguments of the method
			int argSize;
			int numberOfEntries = 0;
			//		codeAttribute.addLocalVariableTableAttribute(this);
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
			int descriptorIndex;
			int nameIndex;
			SourceTypeBinding declaringClassBinding = null;
			final boolean methodDeclarationIsStatic = this.codeStream.methodDeclaration.isStatic();
			if (!methodDeclarationIsStatic) {
				numberOfEntries++;
				if (localContentsOffset + 10 >= this.contents.length) {
					resizeContents(10);
				}
				this.contents[localContentsOffset++] = 0;
				this.contents[localContentsOffset++] = 0;
				this.contents[localContentsOffset++] = (byte) (code_length >> 8);
				this.contents[localContentsOffset++] = (byte) code_length;
				nameIndex = this.constantPool.literalIndex(ConstantPool.This);
				this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
				this.contents[localContentsOffset++] = (byte) nameIndex;
				declaringClassBinding = (SourceTypeBinding) this.codeStream.methodDeclaration.binding.declaringClass;
				descriptorIndex =
					this.constantPool.literalIndex(declaringClassBinding.signature());
				this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
				this.contents[localContentsOffset++] = (byte) descriptorIndex;
				// the resolved position for this is always 0
				this.contents[localContentsOffset++] = 0;
				this.contents[localContentsOffset++] = 0;
			}
			// used to remember the local variable with a generic type
			int genericLocalVariablesCounter = 0;
			LocalVariableBinding[] genericLocalVariables = null;
			int numberOfGenericEntries = 0;

			if (binding.isConstructor()) {
				ReferenceBinding declaringClass = binding.declaringClass;
				if (declaringClass.isNestedType()) {
					NestedTypeBinding methodDeclaringClass = (NestedTypeBinding) declaringClass;
					argSize = methodDeclaringClass.getEnclosingInstancesSlotSize();
					SyntheticArgumentBinding[] syntheticArguments;
					if ((syntheticArguments = methodDeclaringClass.syntheticEnclosingInstances()) != null) {
						for (int i = 0, max = syntheticArguments.length; i < max; i++) {
							LocalVariableBinding localVariable = syntheticArguments[i];
							final TypeBinding localVariableTypeBinding = localVariable.type;
							if (localVariableTypeBinding.isParameterizedType() || localVariableTypeBinding.isTypeVariable()) {
								if (genericLocalVariables == null) {
									// we cannot have more than max locals
									genericLocalVariables = new LocalVariableBinding[max];
								}
								genericLocalVariables[genericLocalVariablesCounter++] = localVariable;
								numberOfGenericEntries++;
							}
							if (localContentsOffset + 10 >= this.contents.length) {
								resizeContents(10);
							}
							// now we can safely add the local entry
							numberOfEntries++;
							this.contents[localContentsOffset++] = 0;
							this.contents[localContentsOffset++] = 0;
							this.contents[localContentsOffset++] = (byte) (code_length >> 8);
							this.contents[localContentsOffset++] = (byte) code_length;
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
				} else {
					argSize = 1;
				}
			} else {
				argSize = binding.isStatic() ? 0 : 1;
			}

			int genericArgumentsCounter = 0;
			int[] genericArgumentsNameIndexes = null;
			int[] genericArgumentsResolvedPositions = null;
			TypeBinding[] genericArgumentsTypeBindings = null;

			if (method.binding != null) {
				TypeBinding[] parameters = method.binding.parameters;
				Argument[] arguments = method.arguments;
				if ((parameters != null) && (arguments != null)) {
					for (int i = 0, max = parameters.length; i < max; i++) {
						TypeBinding argumentBinding = parameters[i];
						if (localContentsOffset + 10 >= this.contents.length) {
							resizeContents(10);
						}
						// now we can safely add the local entry
						numberOfEntries++;
						this.contents[localContentsOffset++] = 0;
						this.contents[localContentsOffset++] = 0;
						this.contents[localContentsOffset++] = (byte) (code_length >> 8);
						this.contents[localContentsOffset++] = (byte) code_length;
						nameIndex = this.constantPool.literalIndex(arguments[i].name);
						this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
						this.contents[localContentsOffset++] = (byte) nameIndex;
						int resolvedPosition = argSize;
						if (argumentBinding.isParameterizedType() || argumentBinding.isTypeVariable()) {
							if (genericArgumentsCounter == 0) {
								// we cannot have more than max locals
								genericArgumentsNameIndexes = new int[max];
								genericArgumentsResolvedPositions = new int[max];
								genericArgumentsTypeBindings = new TypeBinding[max];
							}
							genericArgumentsNameIndexes[genericArgumentsCounter] = nameIndex;
							genericArgumentsResolvedPositions[genericArgumentsCounter] = resolvedPosition;
							genericArgumentsTypeBindings[genericArgumentsCounter++] = argumentBinding;
						}
						descriptorIndex = this.constantPool.literalIndex(argumentBinding.signature());
						this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
						this.contents[localContentsOffset++] = (byte) descriptorIndex;
						switch(argumentBinding.id) {
							case TypeIds.T_long :
							case TypeIds.T_double :
								argSize += 2;
								break;
							default :
								argSize++;
								break;
						}
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

			final boolean currentInstanceIsGeneric =
				!methodDeclarationIsStatic
				&& declaringClassBinding != null
				&& declaringClassBinding.typeVariables != Binding.NO_TYPE_VARIABLES;
			if (genericLocalVariablesCounter != 0 || genericArgumentsCounter != 0 || currentInstanceIsGeneric) {
				// add the local variable type table attribute
				numberOfEntries = numberOfGenericEntries + genericArgumentsCounter + (currentInstanceIsGeneric ? 1 : 0);
				// reserve enough space
				int maxOfEntries = 8 + numberOfEntries * 10;
				if (localContentsOffset + maxOfEntries >= this.contents.length) {
					resizeContents(maxOfEntries);
				}
				int localVariableTypeNameIndex =
					this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTypeTableName);
				this.contents[localContentsOffset++] = (byte) (localVariableTypeNameIndex >> 8);
				this.contents[localContentsOffset++] = (byte) localVariableTypeNameIndex;
				value = numberOfEntries * 10 + 2;
				this.contents[localContentsOffset++] = (byte) (value >> 24);
				this.contents[localContentsOffset++] = (byte) (value >> 16);
				this.contents[localContentsOffset++] = (byte) (value >> 8);
				this.contents[localContentsOffset++] = (byte) value;
				this.contents[localContentsOffset++] = (byte) (numberOfEntries >> 8);
				this.contents[localContentsOffset++] = (byte) numberOfEntries;
				if (currentInstanceIsGeneric) {
					numberOfEntries++;
					this.contents[localContentsOffset++] = 0; // the startPC for this is always 0
					this.contents[localContentsOffset++] = 0;
					this.contents[localContentsOffset++] = (byte) (code_length >> 8);
					this.contents[localContentsOffset++] = (byte) code_length;
					nameIndex = this.constantPool.literalIndex(ConstantPool.This);
					this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
					this.contents[localContentsOffset++] = (byte) nameIndex;
					descriptorIndex = this.constantPool.literalIndex(declaringClassBinding.genericTypeSignature());
					this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
					this.contents[localContentsOffset++] = (byte) descriptorIndex;
					this.contents[localContentsOffset++] = 0;// the resolved position for this is always 0
					this.contents[localContentsOffset++] = 0;
				}

				for (int i = 0; i < genericLocalVariablesCounter; i++) {
					LocalVariableBinding localVariable = genericLocalVariables[i];
					this.contents[localContentsOffset++] = 0;
					this.contents[localContentsOffset++] = 0;
					this.contents[localContentsOffset++] = (byte) (code_length >> 8);
					this.contents[localContentsOffset++] = (byte) code_length;
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
				for (int i = 0; i < genericArgumentsCounter; i++) {
					this.contents[localContentsOffset++] = 0;
					this.contents[localContentsOffset++] = 0;
					this.contents[localContentsOffset++] = (byte) (code_length >> 8);
					this.contents[localContentsOffset++] = (byte) code_length;
					nameIndex = genericArgumentsNameIndexes[i];
					this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
					this.contents[localContentsOffset++] = (byte) nameIndex;
					descriptorIndex = this.constantPool.literalIndex(genericArgumentsTypeBindings[i].genericTypeSignature());
					this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
					this.contents[localContentsOffset++] = (byte) descriptorIndex;
					int resolvedPosition = genericArgumentsResolvedPositions[i];
					this.contents[localContentsOffset++] = (byte) (resolvedPosition >> 8);
					this.contents[localContentsOffset++] = (byte) resolvedPosition;
				}
				attributeNumber++;
			}
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP_TABLE) != 0) {
			StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream) this.codeStream;
			stackMapFrameCodeStream.removeFramePosition(code_length);
			if (stackMapFrameCodeStream.hasFramePositions()) {
				ArrayList frames = new ArrayList();
				traverse(binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
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
					numberOfFrames = 0;
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
						numberOfFrames++;
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

		// update the number of attributes// ensure first that there is enough space available inside the localContents array
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
