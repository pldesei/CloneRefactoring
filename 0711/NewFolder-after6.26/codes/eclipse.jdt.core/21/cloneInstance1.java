				for (int i = 0; i < typeParametersLength; i++) {
					typeParameterNames[i] = typeParameters[i].name;
					TypeReference[] bounds = typeParameters[i].bounds;
					if (bounds != null) {
						int boundLength = bounds.length;
						char[][] boundNames = new char[boundLength][];
						for (int j = 0; j < boundLength; j++) {
							boundNames[j] = 
								CharOperation.concatWith(bounds[j].getParameterizedTypeName(), '.'); 
						}
						typeParameterBounds[i] = boundNames;
					}
				}
