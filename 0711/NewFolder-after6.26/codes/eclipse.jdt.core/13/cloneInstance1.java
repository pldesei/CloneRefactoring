			if (currentChar == '\\' && source[i+1] == 'u') {
				//-------------unicode traitement ------------
				int c1, c2, c3, c4;
				charLength++;
				while (source[i+charLength] == 'u') charLength++;
				if (((c1 = Character.getNumericValue(source[i+charLength++])) > 15
					|| c1 < 0)
					|| ((c2 = Character.getNumericValue(source[i+charLength++])) > 15 || c2 < 0)
					|| ((c3 = Character.getNumericValue(source[i+charLength++])) > 15 || c3 < 0)
					|| ((c4 = Character.getNumericValue(source[i+charLength])) > 15 || c4 < 0)) {
					throw new RuntimeException("Invalid unicode in source at "+i);
				}
				currentChar = (char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4);
				i+=charLength;
			}
