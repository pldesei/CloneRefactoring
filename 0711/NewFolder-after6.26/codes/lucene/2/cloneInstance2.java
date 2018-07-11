  public void get(int docNumber, TermVectorMapper mapper) throws IOException {
    // Check if no term vectors are available for this segment at all
    if (tvx != null) {
      //We need to offset by
      tvx.seek((docNumber * 8L) + FORMAT_SIZE);
      long position = tvx.readLong();

      tvd.seek(position);
      int fieldCount = tvd.readVInt();

      // No fields are vectorized for this document
      if (fieldCount != 0) {
        int number = 0;
        String[] fields = new String[fieldCount];

        for (int i = 0; i < fieldCount; i++) {
          if(tvdFormat == FORMAT_VERSION)
            number = tvd.readVInt();
          else
            number += tvd.readVInt();

          fields[i] = fieldInfos.fieldName(number);
        }

        // Compute position in the tvf file
        position = 0;
        long[] tvfPointers = new long[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
          position += tvd.readVLong();
          tvfPointers[i] = position;
        }

        mapper.setDocumentNumber(docNumber);
        readTermVectors(fields, tvfPointers, mapper);
      }
    } else {
      //System.out.println("No tvx file");
    }
  }
