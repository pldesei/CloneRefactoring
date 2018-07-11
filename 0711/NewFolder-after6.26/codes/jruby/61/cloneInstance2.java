    public static IRubyObject timeout(final ThreadContext context, IRubyObject timeout, IRubyObject seconds, IRubyObject exceptionType, Block block) {
        // No seconds, just yield
        if (seconds.isNil() || Helpers.invoke(context, seconds, "zero?").isTrue()) {
            return block.yieldSpecific(context);
        }

        final Ruby runtime = context.runtime;

        // No timeout in critical section
        if (runtime.getThreadService().getCritical()) {
            return raiseBecauseCritical(context);
        }

        final RubyThread currentThread = context.getThread();
        final AtomicBoolean latch = new AtomicBoolean(false);

        IRubyObject id = new RubyObject(runtime, runtime.getObject());
        RubyClass anonException = (RubyClass)runtime.getClassFromPath("Timeout::AnonymousException");
        Runnable timeoutRunnable = exceptionType.isNil() ?
                prepareRunnable(currentThread, runtime, latch, id) :
                prepareRunnableWithException(currentThread, exceptionType, runtime, latch);
        Future timeoutFuture = null;

        try {
            try {
                timeoutFuture = timeoutExecutor.schedule(timeoutRunnable,
                        (long)(seconds.convertToFloat().getDoubleValue() * 1000000), TimeUnit.MICROSECONDS);

                return block.yield(context, seconds);
            } finally {
                killTimeoutThread(context, timeoutFuture, latch);
            }
        } catch (RaiseException re) {
            // if it's the exception we're expecting
            if (re.getException().getMetaClass() == anonException) {
                // and we were not given a specific exception
                if (exceptionType.isNil()) {
                    // and it's the exception intended for us
                    if (re.getException().getInternalVariable("__identifier__") == id) {
                        return raiseTimeoutError(context, re);
                    }
                }
            }

            // otherwise, rethrow
            throw re;
        }
    }
