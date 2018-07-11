    if (erasureIsClass) {
		while ((currentType = currentType.superclass()) != null) { 
			if (currentType.id == wellKnownErasureID)
				return currentType;
			switch(currentType.kind()) {
				case Binding.PARAMETERIZED_TYPE :
				case Binding.RAW_TYPE :
				case Binding.ARRAY_TYPE :
					if (currentType.erasure().id == wellKnownErasureID) 
						return currentType;
			}
		}    
		return null;
    }
