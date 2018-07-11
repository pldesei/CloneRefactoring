    public void testSerialization() throws IOException, ClassNotFoundException {
        XYCoordinate v1 = new XYCoordinate(1.0, 2.0);
        XYCoordinate v2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(v1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        v2 = (XYCoordinate) in.readObject();
        in.close();
        assertEquals(v1, v2);
    }
