    public void testSerialization() throws IOException, ClassNotFoundException {
        CategoryAxis3D a1 = new CategoryAxis3D("Test Axis");
        CategoryAxis3D a2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();
        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        a2 = (CategoryAxis3D) in.readObject();
        in.close();
        assertEquals(a1, a2);
    }
