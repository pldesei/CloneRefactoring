    public void testSerialization() throws IOException, ClassNotFoundException {
        XYBarRenderer r1 = new XYBarRenderer();
        XYBarRenderer r2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(r1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        r2 = (XYBarRenderer) in.readObject();
        in.close();
        assertEquals(r1, r2);
    }
