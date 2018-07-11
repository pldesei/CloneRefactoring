	} else {
		type = this.resolvedType = getTypeBinding(blockScope);
		if (type == null)
			return null; // detected cycle while resolving hierarchy	
		if (!type.isValidBinding()) {
			reportInvalidType(blockScope);
			return null;
		}
		if (isTypeUseDeprecated(type, blockScope)) {
			reportDeprecatedType(blockScope);
		}
		// check raw type
		if (type.isArrayType()) {
		    TypeBinding leafComponentType = type.leafComponentType();
		    if (leafComponentType.isGenericType()) { // raw type
		        return this.resolvedType = blockScope.createArrayType(blockScope.environment().createRawType((ReferenceBinding)leafComponentType, null), type.dimensions());
		    }
		} else if (type.isGenericType()) {
	        return this.resolvedType = blockScope.environment().createRawType((ReferenceBinding)type, null); // raw type
		}
	}
