    public RubyHash invert() {
        RubyHash result = newHash(getRuntime());

        try {
            preIter();
            RubyHashEntry[]ltable = table;
            for (int i = 0; i < ltable.length; i++) {
                for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                    result.op_aset(entry.value, entry.key);
                }
            }
        } finally {postIter();}

        return result;
    }
