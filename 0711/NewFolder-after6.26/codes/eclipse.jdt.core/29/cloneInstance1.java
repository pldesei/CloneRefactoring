				for (int i = 0, max = types.length; i < max; i++) {
					TypeBinding typeParameter = (TypeBinding) types[i];
					typeParameter.appendParameterKey(buffer);
					ITypeBinding[] bounds = typeParameter.getTypeBounds();
					for (int j = 0, length = bounds.length; j < length; j++) {
						TypeBinding bound = (TypeBinding) bounds[j];
						buffer.append(':');
						bound.appendParameterKey(buffer);
					}
					buffer.append(',');
				}
