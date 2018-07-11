public final boolean jumpOverUnicodeWhiteSpace() throws InvalidInputException {
	//BOOLEAN
	//handle the case of unicode. Jump over the next whiteSpace
	//making startPosition pointing on the next available char
	//On false, the currentCharacter is filled up with a potential
	//correct char

	try {
		this.wasAcr = false;
		int c1, c2, c3, c4;
		int unicodeSize = 6;
		this.currentPosition++;
		while (this.source[this.currentPosition] == 'u') {
			this.currentPosition++;
			unicodeSize++;
		}

		if (((c1 = Character.getNumericValue(this.source[this.currentPosition++])) > 15
			|| c1 < 0)
			|| ((c2 = Character.getNumericValue(this.source[this.currentPosition++])) > 15 || c2 < 0)
			|| ((c3 = Character.getNumericValue(this.source[this.currentPosition++])) > 15 || c3 < 0)
			|| ((c4 = Character.getNumericValue(this.source[this.currentPosition++])) > 15 || c4 < 0)) {
			throw new InvalidInputException(INVALID_UNICODE_ESCAPE);
		}

		this.currentCharacter = (char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4);
		if (CharOperation.isWhitespace(this.currentCharacter))
			return true;

		//buffer the new char which is not a white space
		unicodeStoreAt(++this.withoutUnicodePtr);
		//this.withoutUnicodePtr == 1 is true here
		return false;
	} catch (IndexOutOfBoundsException e){
		this.currentPosition--;
		throw new InvalidInputException(INVALID_UNICODE_ESCAPE);
	}
}
