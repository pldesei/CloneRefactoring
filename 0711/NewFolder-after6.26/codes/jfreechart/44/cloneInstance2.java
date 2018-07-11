    public void testSerialization() throws IOException, ClassNotFoundException {
        ShortTextTitle t1 = new ShortTextTitle("ABC");
        ShortTextTitle t2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(t1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        t2 = (ShortTextTitle) in.readObject();
        in.close();
        assertEquals(t1, t2);
    }
