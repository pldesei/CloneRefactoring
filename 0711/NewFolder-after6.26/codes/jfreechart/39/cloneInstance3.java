    public void testSerialization2() throws IOException, ClassNotFoundException {
        // test a default instance
        DialPointer i1 = new DialPointer.Pointer(1);
        DialPointer i2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(i1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        i2 = (DialPointer) in.readObject();
        in.close();
        assertEquals(i1, i2);

        // test a custom instance
    }
