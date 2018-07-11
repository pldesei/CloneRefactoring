    public static IRubyObject timeout(final ThreadContext context, IRubyObject timeout, IRubyObject seconds, Block block) {
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
        Runnable timeoutRunnable = prepareRunnable(currentThread, runtime, latch, id);
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
            if (re.getException().getInternalVariable("__identifier__") == id) {
                return raiseTimeoutError(context, re);
            } else {
                throw re;
            }
        }
    }
