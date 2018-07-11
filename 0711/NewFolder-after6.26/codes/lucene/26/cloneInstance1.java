  public void finish(FieldInfos fis, int numDocs) throws IOException {
    if (numDocsWritten != numDocs) {
      throw new RuntimeException("mergeFields produced an invalid result: docCount is " + numDocs 
          + " but only saw " + numDocsWritten + " file=" + out.toString() + "; now aborting this merge to prevent index corruption");
    }
    write(END);
    newLine();
    String checksum = Long.toString(out.getChecksum());
    write(CHECKSUM);
    write(checksum);
    newLine();
  }
