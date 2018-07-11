        if (!(proc = interfaceProcs.aref(methodName)).isNil()) {
            RubyObject[] rubyArgs = new RubyObject[args.length];

            for (int i = 0; i < args.length; i++) {
                rubyArgs[i] = JavaUtil.convertJavaToRuby(getRuby(), args[i], method.getParameterTypes()[i]);
            }

            result = ((RubyProc) proc).call(rubyArgs);
        } else if (!(rubyMethod = method(methodName)).isNil()) {
