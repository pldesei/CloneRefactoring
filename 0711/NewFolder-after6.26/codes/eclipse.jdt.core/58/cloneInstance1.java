	for (int i = 0; i < nextPosition; i++) {
		currentType = interfacesToVisit[i];
		if (currentType.id == wellKnownErasureID)
			return currentType;
		switch(currentType.kind()) {
			case Binding.PARAMETERIZED_TYPE :
			case Binding.RAW_TYPE :
			case Binding.ARRAY_TYPE : 
				if (currentType.erasure().id == wellKnownErasureID) 
					return currentType;
		}
		ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
		if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
			int itsLength = itsInterfaces.length;
			if (nextPosition + itsLength >= interfacesToVisit.length)
				System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
			nextInterface : for (int a = 0; a < itsLength; a++) {
				ReferenceBinding next = itsInterfaces[a];
				for (int b = 0; b < nextPosition; b++)
					if (next == interfacesToVisit[b]) continue nextInterface;
				interfacesToVisit[nextPosition++] = next;
			}
		}
	}
