    public boolean scopeUsesVariable(Variable v) { 
        if (_usedVars.contains(v)) {
            return true;
        }
        else {
            for (IRClosure cl: getCFG().getScope().getClosures()) {
                BindingStorePlacementProblem nestedProblem = (BindingStorePlacementProblem)cl.getCFG().getDataFlowSolution(DataFlowConstants.BSP_NAME);
                if (nestedProblem.scopeUsesVariable(v)) 
                    return true;
            }

            return false;
        }
    }
