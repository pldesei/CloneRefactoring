					if (numberOfFrames != 0) {
						this.contents[numberOfFramesOffset++] = (byte) (numberOfFrames >> 8);
						this.contents[numberOfFramesOffset] = (byte) numberOfFrames;

						int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
						this.contents[stackMapTableAttributeLengthOffset++] = (byte) (attributeLength >> 24);
						this.contents[stackMapTableAttributeLengthOffset++] = (byte) (attributeLength >> 16);
						this.contents[stackMapTableAttributeLengthOffset++] = (byte) (attributeLength >> 8);
						this.contents[stackMapTableAttributeLengthOffset] = (byte) attributeLength;
						attributeNumber++;
					} else {
