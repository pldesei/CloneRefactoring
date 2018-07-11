		    if (!otherType.isInterface()) {
				while ((currentType = currentType.superclass()) != null) {
					if (currentType == otherType)
						return currentType;
					switch(currentType.kind()) {
						case Binding.PARAMETERIZED_TYPE :
						case Binding.RAW_TYPE :
						case Binding.ARRAY_TYPE : 
							if (currentType.erasure() == otherType)
								return currentType;
					}					
				}
				return null;
		    }
