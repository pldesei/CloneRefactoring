    public RubyModule rbPublic(IRubyObject[] args) {
        if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
            throw new SecurityError(getRuntime(), "Insecure: can't change method visibility");
        }

        if (args.length == 0) {
            getRuntime().setCurrentVisibility(Visibility.PUBLIC);
        } else {
            setMethodVisibility(args, Visibility.PUBLIC);
        }

        return this;
    }
