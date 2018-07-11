				} else {
					int start = type.getStartPosition();
					ArrayType subarrayType = arrayType;
					int index = extraDimension;
					while (index > 0) {
						subarrayType = (ArrayType) subarrayType.getComponentType();
						index--;
					}
					int end = retrieveProperRightBracketPosition(remainingDimensions, start);
					subarrayType.setSourceRange(start, end - start + 1);
					// cut the child loose from its parent (without creating garbage)
					subarrayType.setParent(null, null);
					updateInnerPositions(subarrayType, remainingDimensions);
					variableDeclarationStatement.setType(subarrayType);
					this.ast.getBindingResolver().updateKey(type, subarrayType);
				}
