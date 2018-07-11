  public static <E extends Exception> void closeWhileHandlingException(E priorException, Iterable<? extends Closeable> objects) throws E, IOException {
    Throwable th = null;

    for (Closeable object : objects) {
      try {
        if (object != null) {
          object.close();
        }
      } catch (Throwable t) {
        addSuppressed((priorException == null) ? th : priorException, t);
        if (th == null) {
          th = t;
        }
      }
    }

    if (priorException != null) {
      throw priorException;
    } else if (th != null) {
      if (th instanceof IOException) throw (IOException) th;
      if (th instanceof RuntimeException) throw (RuntimeException) th;
      if (th instanceof Error) throw (Error) th;
      throw new RuntimeException(th);
    }
  }
