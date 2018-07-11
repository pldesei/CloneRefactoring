    public boolean scopeDefinesVariable(Variable v) { 
        if (_defVars.contains(v)) {
            return true;
        }
        else {
            for (IRClosure cl: getCFG().getScope().getClosures()) {
                BindingStorePlacementProblem nestedProblem = (BindingStorePlacementProblem)cl.getCFG().getDataFlowSolution(DataFlowConstants.BSP_NAME);
                if (nestedProblem.scopeDefinesVariable(v)) 
                    return true;
            }

            return false;
        }
    }
