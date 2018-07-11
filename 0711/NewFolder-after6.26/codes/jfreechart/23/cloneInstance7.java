    public void testSerialization() throws IOException, ClassNotFoundException {
        ColorBar a1 = new ColorBar("Test Axis");
        ColorBar a2 = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        a2 = (ColorBar) in.readObject();
        in.close();
        assertEquals(a1, a2);
    }
