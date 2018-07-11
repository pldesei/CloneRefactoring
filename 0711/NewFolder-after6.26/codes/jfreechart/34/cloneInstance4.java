    public void testSerialization() throws IOException, ClassNotFoundException {
        PiePlot3D p1 = new PiePlot3D(null);
        PiePlot3D p2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(p1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        p2 = (PiePlot3D) in.readObject();
        in.close();
        assertEquals(p1, p2);
    }
