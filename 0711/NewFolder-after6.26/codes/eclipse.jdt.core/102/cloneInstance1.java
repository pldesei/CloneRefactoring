	public ReferenceBinding setSuperClass(ReferenceBinding superclass) {
		this.superclass = superclass;
		if ((this.tagBits & TagBits.HasAnnotatedVariants) != 0) {
			TypeBinding [] annotatedTypes = this.environment.getAnnotatedTypes(this);
			for (int i = 0, length = annotatedTypes == null ? 0 : annotatedTypes.length; i < length; i++) {
				TypeVariableBinding annotatedType = (TypeVariableBinding) annotatedTypes[i];
				annotatedType.superclass = superclass;
			}
		}
		return superclass;
	}
