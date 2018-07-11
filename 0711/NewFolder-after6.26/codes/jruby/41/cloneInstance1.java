        for (int i = 1; i < args.length; i++) {
            RubyString filename = get_path(context, args[i]);
            
            if (!RubyFileTest.exist_p(filename, filename).isTrue()) {
                throw runtime.newErrnoENOENTError(filename.toString());
            }
            
            boolean result = 0 == runtime.getPosix().chmod(filename.getUnicodeValue(), (int)mode.getLongValue());
            if (result) {
                count++;
            }
        }
