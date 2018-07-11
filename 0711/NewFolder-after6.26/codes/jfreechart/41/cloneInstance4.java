    public void testSerialization() throws IOException, ClassNotFoundException {
        LineRenderer3D r1 = new LineRenderer3D();
        LineRenderer3D r2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(r1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        r2 = (LineRenderer3D) in.readObject();
        in.close();
        assertEquals(r1, r2);
    }
