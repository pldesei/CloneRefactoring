    public void testSerialization() throws IOException, ClassNotFoundException {
        LevelRenderer r1 = new LevelRenderer();
        LevelRenderer r2;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(r1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        r2 = (LevelRenderer) in.readObject();
        in.close();
        assertEquals(r1, r2);
    }
