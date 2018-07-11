	public IRubyObject internalCall(ThreadContext context, IRubyObject receiver, RubyModule lastClass, String name, IRubyObject[] args, boolean noSuper) {
        IRuby runtime = context.getRuntime();
        arity.checkArity(runtime, args);
        try {
            return call(receiver,args);
        } catch(RaiseException e) {
            throw e;
        } catch(JumpException e) {
            throw e;
        } catch(ThreadKill e) {
            throw e;
        } catch(MainExitException e) {
            throw e;
        } catch(Exception e) {
            receiver.getRuntime().getJavaSupport().handleNativeException(e);
            return receiver.getRuntime().getNil();
        }
    }
