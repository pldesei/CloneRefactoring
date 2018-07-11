    public List<Variable<IRubyObject>> getInternalVariableList() {
        VariableTableEntry[] table = variableTableGetTable();
        ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
        IRubyObject readValue;
        for (int i = table.length; --i >= 0; ) {
            for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                if (!isRubyVariable(e.name)) {
                    if ((readValue = e.value) == null) readValue = variableTableReadLocked(e);
                    list.add(new VariableEntry<IRubyObject>(e.name, readValue));
                }
            }
        }
        return list;
    }
