    public static RubyBoolean file_p(IRubyObject recv, IRubyObject filename) {
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
                java.util.zip.ZipEntry e = jf.getEntry(after);
                if (e == null || e.isDirectory()) {
                    return recv.getRuntime().getFalse();
                } else {
                    return recv.getRuntime().getTrue();
                }
            } catch(Exception e) {
                return recv.getRuntime().getFalse();
            }
        }
        JRubyFile file = file(filename);

        return filename.getRuntime().newBoolean(file.exists() && file.isFile());
    }
