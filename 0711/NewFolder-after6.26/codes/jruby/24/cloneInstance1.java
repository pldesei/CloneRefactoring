    public List<Variable<IRubyObject>> getClassVariableList() {
        ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
        VariableTableEntry[] table = variableTableGetTable();
        IRubyObject readValue;
        for (int i = table.length; --i >= 0; ) {
            for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                if (IdUtil.isClassVariable(e.name)) {
                    if ((readValue = e.value) == null) readValue = variableTableReadLocked(e);
                    list.add(new VariableEntry<IRubyObject>(e.name, readValue));
                }
            }
        }
        return list;
    }
