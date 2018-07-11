public final int getNextChar() {
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
				return -1;
			}

			this.currentCharacter = (char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4);

			this.unicodeAsBackSlash = this.currentCharacter == '\\';

			//need the unicode buffer
			if (this.withoutUnicodePtr == 0) {
				//buffer all the entries that have been left aside....
			    unicodeInitializeBuffer(this.currentPosition - unicodeSize - this.startPosition);
			}
			//fill the buffer with the char
			unicodeStoreAt(++this.withoutUnicodePtr);
			return this.currentCharacter;

		} //-------------end unicode traitement--------------
		else {
			this.unicodeAsBackSlash = false;
			if (this.withoutUnicodePtr != 0) {
			    unicodeStoreAt(++this.withoutUnicodePtr);
			}
			return this.currentCharacter;
		}
	} catch (IndexOutOfBoundsException e) {
		return -1;
	}
}
