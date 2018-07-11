    public void testSerialization() throws IOException, ClassNotFoundException {
        XYBlockRenderer r1 = new XYBlockRenderer();
        XYBlockRenderer r2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(r1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        r2 = (XYBlockRenderer) in.readObject();
        in.close();
        assertEquals(r1, r2);
    }
