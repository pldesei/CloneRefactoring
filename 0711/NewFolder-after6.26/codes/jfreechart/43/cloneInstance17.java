    public void testSerialization() throws IOException, ClassNotFoundException {
        DeviationRenderer r1 = new DeviationRenderer();
        DeviationRenderer r2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(r1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        r2 = (DeviationRenderer) in.readObject();
        in.close();
        assertEquals(r1, r2);
    }
