	if (walker != TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
		final int depth = binding.depth();
		AnnotationBinding [][] annotations = null;
		for (int i = 0; i <= depth; i++) {
			AnnotationBinding[] annots = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(), this, missingTypeNames);
			if (annots != null && annots.length > 0) {
				if (annotations == null)
					annotations = new AnnotationBinding[depth + 1][];
				annotations[i] = annots;
			}
			walker = walker.toNextNestedType();
		}
		if (annotations != null)
			binding = (ReferenceBinding) createAnnotatedType(binding, annotations);
	}
