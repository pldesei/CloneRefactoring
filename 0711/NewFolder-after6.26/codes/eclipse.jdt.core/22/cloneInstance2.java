	} else {
		type = this.resolvedType = getTypeBinding(classScope);
		if (type == null)
			return null; // detected cycle while resolving hierarchy	
		if (!type.isValidBinding()) {
			reportInvalidType(classScope);
			return null;
		}
		if (isTypeUseDeprecated(type, classScope)) {
			reportDeprecatedType(classScope);
		}
		// check raw type
		if (type.isArrayType()) {
		    TypeBinding leafComponentType = type.leafComponentType();
		    if (leafComponentType.isGenericType()) { // raw type
		        return this.resolvedType = classScope.createArrayType(classScope.environment().createRawType((ReferenceBinding)leafComponentType, null), type.dimensions());
		    }
		} else if (type.isGenericType()) {
	        return this.resolvedType = classScope.environment().createRawType((ReferenceBinding)type, null); // raw type
		}
	}
