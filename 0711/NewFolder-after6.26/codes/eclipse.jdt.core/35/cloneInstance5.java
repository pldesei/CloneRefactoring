public final boolean getNextCharAsDigit(int radix) {
	//BOOLEAN
	//handle the case of unicode.
	//when a unicode appears then we must use a buffer that holds char internal values
	//At the end of this method currentCharacter holds the new visited char
	//and currentPosition points right next after it
	//Both previous lines are true if the currentCharacter is a digit base on radix
	//On false, no side effect has occured.

	//ALL getNextChar.... ARE OPTIMIZED COPIES 
	if (this.currentPosition >= this.source.length) // handle the obvious case upfront
		return false;

	int temp = this.currentPosition;
	try {
		if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
			&& (this.source[this.currentPosition] == 'u')) {
			//-------------unicode traitement ------------
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
				this.currentPosition = temp;
				return false;
			}

			this.currentCharacter = (char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4);
			if (Character.digit(this.currentCharacter, radix) == -1) {
				this.currentPosition = temp;
				return false;
			}

			//need the unicode buffer
			if (this.withoutUnicodePtr == 0) {
				//buffer all the entries that have been left aside....
				unicodeInitializeBuffer(this.currentPosition - unicodeSize - this.startPosition);
			}
			//fill the buffer with the char
			unicodeStoreAt(++this.withoutUnicodePtr);
			return true;
		} //-------------end unicode traitement--------------
		else {
			if (Character.digit(this.currentCharacter, radix) == -1) {
				this.currentPosition = temp;
				return false;
			}
			if (this.withoutUnicodePtr != 0)
				unicodeStoreAt(++this.withoutUnicodePtr);
			return true;
		}
	} catch (IndexOutOfBoundsException e) {
		this.currentPosition = temp;
		return false;
	}
}
