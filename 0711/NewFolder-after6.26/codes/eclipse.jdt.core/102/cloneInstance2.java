	public ReferenceBinding [] setSuperInterfaces(ReferenceBinding[] superInterfaces) {
		this.superInterfaces = superInterfaces;
		if ((this.tagBits & TagBits.HasAnnotatedVariants) != 0) {
			TypeBinding [] annotatedTypes = this.environment.getAnnotatedTypes(this);
			for (int i = 0, length = annotatedTypes == null ? 0 : annotatedTypes.length; i < length; i++) {
				TypeVariableBinding annotatedType = (TypeVariableBinding) annotatedTypes[i];
				annotatedType.superInterfaces = superInterfaces;
			}
		}
		return superInterfaces;
	}
