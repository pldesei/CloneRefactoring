    private static double obj2dbl(Ruby runtime, IRubyObject o) {
        if(o instanceof RubyString) {
            String str = o.asJavaString();
            double d = RubyNumeric.num2dbl(o.convertToFloat());
            if(str.matches("^\\s*[-+]?\\s*[0-9].*")) {
                return d;
            } else {
                throw runtime.newTypeError("array item not a float");
            }
        }

        return RubyKernel.new_float(o, o).getDoubleValue();
    }
