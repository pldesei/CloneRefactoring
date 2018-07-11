    public void UnresolvedSuperInstr(UnresolvedSuperInstr unresolvedsuperinstr) {

        IRBytecodeAdapter m = jvmMethod();
        String name = unresolvedsuperinstr.getMethodAddr().getName();
        Operand[] args = unresolvedsuperinstr.getCallArgs();

        m.loadContext();
        m.loadSelf(); // TODO: get rid of caller
        m.loadSelf();
        // this would be getDefiningModule but that is not used for unresolved super
//        visit(unresolvedsuperinstr.getDefiningModule());
        jvmAdapter().aconst_null();

//        // TODO: CON: is this safe?
//        jvmAdapter().checkcast(p(RubyClass.class));

        for (int i = 0; i < args.length; i++) {
            Operand operand = args[i];
            visit(operand);
        }

        // if there's splats, provide a map and let the call site sort it out
        boolean[] splatMap = IRRuntimeHelpers.buildSplatMap(args, unresolvedsuperinstr.containsArgSplat());

        Operand closure = unresolvedsuperinstr.getClosureArg(null);
        boolean hasClosure = closure != null;
        if (hasClosure) {
            m.loadContext();
            visit(closure);
            m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
        }

        m.invokeUnresolvedSuper(name, args.length, hasClosure, splatMap);

        jvmStoreLocal(unresolvedsuperinstr.getResult());
    }
