    public IRubyObject remove_class_variable(IRubyObject name) {
        String id = name.asSymbol();

        if (!IdUtil.isClassVariable(id)) {
            throw new NameError(getRuntime(), "wrong class variable name " + id);
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
        throw new NameError(getRuntime(), "class variable " + id + " not defined for " + getName());
    }
