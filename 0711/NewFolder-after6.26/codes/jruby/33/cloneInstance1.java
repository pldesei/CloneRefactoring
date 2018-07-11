    public IRubyObject put_array_of_float(ThreadContext context, IRubyObject offset, IRubyObject arrParam) {
        RubyArray arr = (RubyArray) arrParam;
        int count = arr.getLength();
        checkBounds(context, offset, count * 4);
        float[] array = new float[count];
        for (int i = 0; i < array.length; ++i) {
            array[i] = Util.floatValue((IRubyObject) arr.entry(i));
        }
        getMemoryIO().put(getOffset(offset), array, 0, array.length);
        return this;
    }
