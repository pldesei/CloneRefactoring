    public IRubyObject put_array_of_float64(ThreadContext context, IRubyObject offset, IRubyObject arrParam) {
        RubyArray arr = (RubyArray) arrParam;
        int count = arr.getLength();
        checkBounds(context, offset, count * 8);
        double[] array = new double[count];
        for (int i = 0; i < array.length; ++i) {
            array[i] = Util.doubleValue((IRubyObject) arr.entry(i));
        }
        getMemoryIO().put(getOffset(offset), array, 0, array.length);
        return this;
    }
