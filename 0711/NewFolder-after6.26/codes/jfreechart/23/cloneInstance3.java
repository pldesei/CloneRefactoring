    public void testSerialization() throws IOException, ClassNotFoundException {
        DateAxis a1 = new DateAxis("Test Axis");
        DateAxis a2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        a2 = (DateAxis) in.readObject();
        in.close();
        assertEquals(a1, a2);
    }
