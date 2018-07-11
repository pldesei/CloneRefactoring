    public boolean scopeDefinesVariable(Variable v) { 
        if (_defVars.contains(v)) {
            return true;
        }
        else {
            for (IRClosure cl: getCFG().getScope().getClosures()) {
                BindingLoadPlacementProblem nestedProblem = (BindingLoadPlacementProblem)cl.getCFG().getDataFlowSolution(DataFlowConstants.BLP_NAME);
                if (nestedProblem.scopeDefinesVariable(v)) 
                    return true;
            }

            return false;
        }
    }
