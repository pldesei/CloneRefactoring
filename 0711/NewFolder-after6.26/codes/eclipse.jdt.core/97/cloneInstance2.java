		if ((length = interfaces.length) != 0) {
			for (int i = length; --i >= 0;) {
				ReferenceBinding resolveType = interfaces[i];
				long superNullTagBits = NullAnnotationMatching.validNullTagBits(resolveType.tagBits);
				if (superNullTagBits != 0L) {
					if (nullTagBits == 0L) {
						nullTagBits |= superNullTagBits;
					} else if (superNullTagBits != nullTagBits) {
						// not finding either bound or ann should be considered a compiler bug
						TypeReference bound = findBound(this.firstBound, parameter);
						Annotation ann = bound.findAnnotation(superNullTagBits);
						scope.problemReporter().contradictoryNullAnnotationsOnBounds(ann, nullTagBits);
						this.tagBits &= ~TagBits.AnnotationNullMASK;
					}
				}
				interfaces[i] = resolveType;
			}
		}
