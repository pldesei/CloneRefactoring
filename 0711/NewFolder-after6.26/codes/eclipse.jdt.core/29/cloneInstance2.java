						for (int i = 0; i < typeParametersLength; i++) {
							TypeBinding typeParameter = (TypeBinding) typeParameters[i];
							typeParameter.appendParameterKey(buffer);
							ITypeBinding[] bounds = typeParameter.getTypeBounds();
							for (int j = 0, length = bounds.length; j < length; j++) {
								TypeBinding bound = (TypeBinding) bounds[j];
								buffer.append(':');
								bound.appendParameterKey(buffer);
							}
							buffer.append(',');
						}
