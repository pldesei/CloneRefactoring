    public void testSerialization() throws IOException, ClassNotFoundException {
        StackedBarRenderer r1 = new StackedBarRenderer();
        StackedBarRenderer r2;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(r1);
        out.close();
        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        r2 = (StackedBarRenderer) in.readObject();
        in.close();
        assertEquals(r1, r2);
    }
