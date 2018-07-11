    public void ClassSuperInstr(ClassSuperInstr classsuperinstr) {
        // TODO: Nearly identical to InstanceSuperInstr
        IRBytecodeAdapter m = jvmMethod();
        String name = classsuperinstr.getMethodAddr().getName();
        Operand[] args = classsuperinstr.getCallArgs();

        m.loadContext();
        m.loadSelf(); // TODO: get rid of caller
        m.loadSelf();
        visit(classsuperinstr.getDefiningModule());

        // TODO: CON: is this safe?
        jvmAdapter().checkcast(p(RubyClass.class));

        for (int i = 0; i < args.length; i++) {
            Operand operand = args[i];
            visit(operand);
        }

        // if there's splats, provide a map and let the call site sort it out
        boolean[] splatMap = IRRuntimeHelpers.buildSplatMap(args, classsuperinstr.containsArgSplat());

        Operand closure = classsuperinstr.getClosureArg(null);
        boolean hasClosure = closure != null;
        if (hasClosure) {
            m.loadContext();
            visit(closure);
            m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
        }

        m.invokeClassSuper(name, args.length, hasClosure, splatMap);

        jvmStoreLocal(classsuperinstr.getResult());
    }
