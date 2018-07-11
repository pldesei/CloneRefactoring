	if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
		if ((this.binding.tagBits & TagBits.IsNullnessKnown) == 0) {
			// not interested in reporting problems against this.binding:
			new ImplicitNullAnnotationVerifier(scope.environment(), compilerOptions.inheritNullAnnotations)
					.checkImplicitNullAnnotations(this.binding, null/*srcMethod*/, false, scope);
		}
		if (compilerOptions.sourceLevel >= ClassFileConstants.JDK1_8) {
			if (this.binding instanceof ParameterizedGenericMethodBinding && this.typeArguments != null) {
				TypeVariableBinding[] typeVariables = this.binding.original().typeVariables();
				for (int i = 0; i < this.typeArguments.length; i++)
					this.typeArguments[i].checkNullConstraints(scope, (ParameterizedGenericMethodBinding) this.binding, typeVariables, i);
			}
		}
	}
