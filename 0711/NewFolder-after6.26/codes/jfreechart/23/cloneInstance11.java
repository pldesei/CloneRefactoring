    public void testSerialization() throws IOException, ClassNotFoundException {
        NumberAxis3D a1 = new NumberAxis3D("Test Axis");
        NumberAxis3D a2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();

        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        a2 = (NumberAxis3D) in.readObject();
        in.close();
        assertEquals(a1, a2);
    }
