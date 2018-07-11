    public void loadScript(String scriptName, Reader source, boolean wrap) {
        IRubyObject self = getTopSelf();

        ThreadContext context = getCurrentContext();

        context.pushDynamicVars();

        RubyModule wrapper = context.getWrapper();
		RubyModule oldParent;

        if (!wrap) {
            secure(4); /* should alter global state */
            oldParent = context.setRubyClass(objectClass);
            context.setWrapper(null);
        } else {
            /* load in anonymous module as toplevel */
            context.setWrapper(RubyModule.newModule(this, null));
            oldParent = context.setRubyClass(context.getWrapper());
            self = getTopSelf().rbClone();
            self.extendObject(context.getRubyClass());
        }

        context.pushFrame(self, IRubyObject.NULL_ARRAY, null, null);
        context.pushScope();

        /* default visibility is private at loading toplevel */
        setCurrentVisibility(Visibility.PRIVATE);

        try {
        	Node node = parse(source, scriptName);
            self.eval(node);
        } catch (JumpException je) {
        	if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
        		// Make sure this does not bubble out to java caller.
        	} else {
        		throw je;
        	}
        } finally {
            context.popScope();
            context.popFrame();
            context.setRubyClass(oldParent);
            context.popDynamicVars();
            context.setWrapper(wrapper);
        }
    }
