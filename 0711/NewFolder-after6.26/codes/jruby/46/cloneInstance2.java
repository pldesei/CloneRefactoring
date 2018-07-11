    public boolean scopeUsesVariable(Variable v) { 
        if (_usedVars.contains(v)) {
            return true;
        }
        else {
            for (IRClosure cl: getCFG().getScope().getClosures()) {
                BindingLoadPlacementProblem nestedProblem = (BindingLoadPlacementProblem)cl.getCFG().getDataFlowSolution(DataFlowConstants.BLP_NAME);
                if (nestedProblem.scopeUsesVariable(v)) 
                    return true;
            }

            return false;
        }
    }
