    public void testSerialization() throws IOException, ClassNotFoundException {
        PiePlot p1 = new PiePlot(null);
        PiePlot p2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(p1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        p2 = (PiePlot) in.readObject();
        in.close();
        assertEquals(p1, p2);
    }
