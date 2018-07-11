public final int getNextChar(char testedChar1, char testedChar2) {
	//INT 0 : testChar1 \\\\///\\\\ 1 : testedChar2 \\\\///\\\\ -1 : others
	//test can be done with (x==0) for the first and (x>0) for the second
	//handle the case of unicode.
	//when a unicode appears then we must use a buffer that holds char internal values
	//At the end of this method currentCharacter holds the new visited char
	//and currentPosition points right next after it
	//Both previous lines are true if the currentCharacter is == to the testedChar1/2
	//On false, no side effect has occured.

	//ALL getNextChar.... ARE OPTIMIZED COPIES 
	if (this.currentPosition >= this.source.length) // handle the obvious case upfront
		return -1;

	int temp = this.currentPosition;
	try {
		int result;
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
				return 2;
			}

			this.currentCharacter = (char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4);
			if (this.currentCharacter == testedChar1)
				result = 0;
			else
				if (this.currentCharacter == testedChar2)
					result = 1;
				else {
					this.currentPosition = temp;
					return -1;
				}

			//need the unicode buffer
			if (this.withoutUnicodePtr == 0) {
				//buffer all the entries that have been left aside....
				unicodeInitializeBuffer(this.currentPosition - unicodeSize - this.startPosition);
			}
			//fill the buffer with the char
			unicodeStoreAt(++this.withoutUnicodePtr);
			return result;
		} //-------------end unicode traitement--------------
		else {
			if (this.currentCharacter == testedChar1)
				result = 0;
			else
				if (this.currentCharacter == testedChar2)
					result = 1;
				else {
					this.currentPosition = temp;
					return -1;
				}

			if (this.withoutUnicodePtr != 0)
				unicodeStoreAt(++this.withoutUnicodePtr);
			return result;
		}
	} catch (IndexOutOfBoundsException e) {
		this.currentPosition = temp;
		return -1;
	}
}
