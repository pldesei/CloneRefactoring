This clone instance is located in File: 
guava-tests/test/com/google/common/util/concurrent/WrappingScheduledExecutorServiceTest.java
The line range of this clone instance is: 126-131
The content of this clone instance is as follows:
    void assertLastMethodCalled(String method, long initialDelay, long delay, TimeUnit unit) {
      assertEquals(method, lastMethodCalled);
      assertEquals(initialDelay, lastInitialDelay);
      assertEquals(delay, lastDelay);
      assertEquals(unit, lastUnit);
    }
