    public RubyHash each_pair(Block block) {
        Ruby runtime = getRuntime();
        ThreadContext context = runtime.getCurrentContext();

        try {
            preIter();
            RubyHashEntry[]ltable = table;
            for (int i = 0; i < ltable.length; i++) {
                for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                    // rb_yield_values(2,...) equivalent
                    block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, true);
                }
            }
        } finally {postIter();}

        return this;	
    }
