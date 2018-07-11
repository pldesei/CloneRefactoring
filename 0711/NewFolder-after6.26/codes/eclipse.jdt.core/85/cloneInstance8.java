					if (numberOfFrames != 0) {
						this.contents[numberOfFramesOffset++] = (byte) (numberOfFrames >> 8);
						this.contents[numberOfFramesOffset] = (byte) numberOfFrames;

						int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
						this.contents[stackMapAttributeLengthOffset++] = (byte) (attributeLength >> 24);
						this.contents[stackMapAttributeLengthOffset++] = (byte) (attributeLength >> 16);
						this.contents[stackMapAttributeLengthOffset++] = (byte) (attributeLength >> 8);
						this.contents[stackMapAttributeLengthOffset] = (byte) attributeLength;
						attributeNumber++;
					} else {
