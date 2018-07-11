        if (isDFBarrier) {
            // All dirty unboxed (local) vars will get reboxed.
            for (Variable v: state.unboxedDirtyVars) {
                if (v instanceof LocalVariable) {
                    varsToBox.add(v);
                }
            }

            // We have to re-unbox local variables as necessary since we don't
            // know how they are going to change once we get past this instruction.
            List<Variable> lvs = new ArrayList<Variable>();
            for (Variable v: state.unboxedVars.keySet()) {
                if (v instanceof LocalVariable) {
                    lvs.add(v);
                }
            }
            state.unboxedVars.keySet().removeAll(lvs);
        }
