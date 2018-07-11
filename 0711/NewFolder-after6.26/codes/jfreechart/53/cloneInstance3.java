    public void testSerialization() throws IOException, ClassNotFoundException {
        Vector v1 = new Vector(1.0, 2.0);
        Vector v2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(v1);
        out.close();

        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        v2 = (Vector) in.readObject();
        in.close();
        assertEquals(v1, v2);
    }
