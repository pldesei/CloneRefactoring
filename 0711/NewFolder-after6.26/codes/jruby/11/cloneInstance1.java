    public RubyHash each(Block block) {
        Ruby runtime = getRuntime();
        ThreadContext context = runtime.getCurrentContext();

        try {
            preIter();
            RubyHashEntry[]ltable = table;
            for (int i = 0; i < ltable.length; i++) {
                for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                    // rb_assoc_new equivalent
                    block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, false);
                }
            }
        } finally {postIter();}

        return this;
    }
