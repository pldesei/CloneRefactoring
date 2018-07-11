    public void testSerialization() throws IOException, ClassNotFoundException {
        XYDifferenceRenderer r1 = new XYDifferenceRenderer(Color.red,
                Color.blue, false);
        XYDifferenceRenderer r2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(r1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        r2 = (XYDifferenceRenderer) in.readObject();
        in.close();
        assertEquals(r1, r2);
    }
