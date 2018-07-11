    public static IRubyObject exist_p(IRubyObject recv, IRubyObject filename) {
        if (Ruby.isSecurityRestricted()) {
            return recv.getRuntime().getFalse();
        }

        if(filename.convertToString().toString().startsWith("file:")) {
            String file = filename.convertToString().toString().substring(5);
            int bang = file.indexOf('!');
            if (bang == -1 || bang == file.length() - 1) {
                return recv.getRuntime().getFalse();
            }
            String jar = file.substring(0, bang);
            String after = file.substring(bang + 2);
            try {
                java.util.jar.JarFile jf = new java.util.jar.JarFile(jar);
                if(jf.getJarEntry(after) != null) {
                    return recv.getRuntime().getTrue();
                } else {
                    return recv.getRuntime().getFalse();
                }
            } catch(Exception e) {
                return recv.getRuntime().getFalse();
            }
        }

        return recv.getRuntime().newBoolean(file(filename).exists());
    }
