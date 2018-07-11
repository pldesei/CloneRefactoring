    public void testSerialization1() throws IOException, ClassNotFoundException {
        StrokeMap m1 = new StrokeMap();
        StrokeMap m2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(m1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        m2 = (StrokeMap) in.readObject();
        in.close();
        assertEquals(m1, m2);
    }
