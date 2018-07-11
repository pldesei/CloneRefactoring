    public void ZSuperInstr(ZSuperInstr zsuperinstr) {
        // TODO: Nearly identical to InstanceSuperInstr
        IRBytecodeAdapter m = jvmMethod();
        String name = zsuperinstr.getMethodAddr().getName(); // ignored on the other side since it is unresolved
        Operand[] args = zsuperinstr.getCallArgs();

        m.loadContext();
        m.loadSelf(); // TODO: get rid of caller
        m.loadSelf();
        jvmAdapter().aconst_null(); // no defining class

        // TODO: CON: is this safe?
        jvmAdapter().checkcast(p(RubyClass.class));

        for (int i = 0; i < args.length; i++) {
            Operand operand = args[i];
            visit(operand);
        }

        // if there's splats, provide a map and let the call site sort it out
        boolean[] splatMap = IRRuntimeHelpers.buildSplatMap(args, zsuperinstr.containsArgSplat());

        Operand closure = zsuperinstr.getClosureArg(null);
        boolean hasClosure = closure != null;
        if (hasClosure) {
            m.loadContext();
            visit(closure);
            m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
        }

        m.invokeZSuper(name, args.length, hasClosure, splatMap);

        jvmStoreLocal(zsuperinstr.getResult());
    }
