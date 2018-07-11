    public void testSerialization() throws IOException, ClassNotFoundException {
        LogAxis a1 = new LogAxis("Test Axis");
        LogAxis a2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        a2 = (LogAxis) in.readObject();
        in.close();
        assertEquals(a1, a2);
    }
