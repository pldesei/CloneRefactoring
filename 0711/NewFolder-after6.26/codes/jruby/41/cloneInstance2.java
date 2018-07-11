        for (int i = 2; i < args.length; i++) {
            RubyString filename = get_path(context, args[i]);

            if (!RubyFileTest.exist_p(filename, filename).isTrue()) {
                throw runtime.newErrnoENOENTError(filename.toString());
            }
            
            boolean result = 0 == runtime.getPosix().chown(filename.getUnicodeValue(), owner, group);
            if (result) {
                count++;
            }
        }
