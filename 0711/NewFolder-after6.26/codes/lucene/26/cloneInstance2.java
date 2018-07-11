  public void finish(FieldInfos fis, int numDocs) throws IOException {
    if (numDocsWritten != numDocs) {
      throw new RuntimeException("mergeVectors produced an invalid result: mergedDocs is " + numDocs + " but vec numDocs is " + numDocsWritten + " file=" + out.toString() + "; now aborting this merge to prevent index corruption");
    }
    write(END);
    newLine();
    String checksum = Long.toString(out.getChecksum());
    write(CHECKSUM);
    write(checksum);
    newLine();
  }
