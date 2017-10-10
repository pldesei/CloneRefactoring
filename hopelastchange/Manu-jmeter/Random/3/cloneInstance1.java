(startLine=130 endLine=142 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/src/jorphan/org/apache/jorphan/logging/Slf4jLogkitLogger.java)
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
