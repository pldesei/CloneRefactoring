    private static float obj2flt(Ruby runtime, IRubyObject o) {
        if(o instanceof RubyString) {
            String str = o.asJavaString();
            float f = (float)RubyNumeric.num2dbl(o.convertToFloat());
            if(str.matches("^\\s*[-+]?\\s*[0-9].*")) {
                return f;
            } else {
                throw runtime.newTypeError("array item not a float");
            }
        }

        return (float)RubyKernel.new_float(o, o).getDoubleValue();
    }
