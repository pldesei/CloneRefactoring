    public void testSerialization() throws IOException, ClassNotFoundException {
        NumberAxis a1 = new NumberAxis("Test Axis");
        NumberAxis a2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        a2 = (NumberAxis) in.readObject();
        in.close();
        assertEquals(a1, a2);
    }
