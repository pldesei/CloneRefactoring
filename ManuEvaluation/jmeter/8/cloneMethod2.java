This clone method is located in File: 
src/jorphan/org/apache/jorphan/logging/Slf4jLogkitLogger.java
The line range of this clone method is: 145-157
The content of this clone method is as follows:
    public void log(Priority priority, String message) {
        if (priority == Priority.FATAL_ERROR) {
            slf4jLogger.error(message);
        } else if (priority == Priority.ERROR) {
            slf4jLogger.error(message);
        } else if (priority == Priority.WARN) {
            slf4jLogger.warn(message);
        } else if (priority == Priority.INFO) {
            slf4jLogger.info(message);
        } else if (priority == Priority.DEBUG) {
            slf4jLogger.debug(message);
        }
    }
