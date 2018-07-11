    public IRubyObject remove_const(IRubyObject name) {
        String id = name.asSymbol();

        if (!IdUtil.isConstant(id)) {
            throw new NameError(getRuntime(), "wrong constant name " + id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw new SecurityError(getRuntime(), "Insecure: can't remove class variable");
        }
        if (isFrozen()) {
            throw new FrozenError(getRuntime(), "class/module");
        }

        if (hasInstanceVariable(id)) {
            return removeInstanceVariable(id);
        }

        if (isClassVarDefined(id)) {
            throw new NameError(getRuntime(), "cannot remove " + id + " for " + getName());
        }
        throw new NameError(getRuntime(), "constant " + id + " not defined for " + getName());
    }
