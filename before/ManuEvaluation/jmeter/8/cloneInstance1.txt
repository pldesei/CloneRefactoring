This clone instance is located in File: 
src/jorphan/org/apache/jorphan/logging/Slf4jLogkitLogger.java
The line range of this clone instance is: 130-142
The content of this clone instance is as follows:
    public void log(Priority priority, String message, Throwable throwable) {
        if (priority == Priority.FATAL_ERROR) {
            slf4jLogger.error(message, throwable);
        } else if (priority == Priority.ERROR) {
            slf4jLogger.error(message, throwable);
        } else if (priority == Priority.WARN) {
            slf4jLogger.warn(message, throwable);
        } else if (priority == Priority.INFO) {
            slf4jLogger.info(message, throwable);
        } else if (priority == Priority.DEBUG) {
            slf4jLogger.debug(message, throwable);
        }
    }
