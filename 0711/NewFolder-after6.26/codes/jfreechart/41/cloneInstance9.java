    public void testSerialization() throws IOException, ClassNotFoundException {
        StackedAreaRenderer r1 = new StackedAreaRenderer();
        StackedAreaRenderer r2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(r1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        r2 = (StackedAreaRenderer) in.readObject();
        in.close();
        assertEquals(r1, r2);
    }
