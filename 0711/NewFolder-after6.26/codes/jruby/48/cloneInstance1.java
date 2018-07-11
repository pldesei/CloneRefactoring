        if(o instanceof RubyString) {
            String str = o.asJavaString();
            float f = (float)RubyNumeric.num2dbl(o.convertToFloat());
            if(str.matches("^\\s*[-+]?\\s*[0-9].*")) {
                return f;
            } else {
                throw runtime.newTypeError("array item not a float");
            }
        }
