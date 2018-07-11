    public IRubyObject index(IRubyObject value) {
        Ruby runtime = getRuntime();
        ThreadContext context = runtime.getCurrentContext();

        try {
            preIter();
            RubyHashEntry[]ltable = table;
            for (int i = 0; i < ltable.length; i++) {
                for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                    if (equalInternal(context, entry.value, value).isTrue()) return entry.key;
                }
            }
        } finally {postIter();}

        return getRuntime().getNil();
    }
