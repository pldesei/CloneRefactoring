    public void testSerialization() throws IOException, ClassNotFoundException {
        ContourPlot p1 = new ContourPlot(null, null, null, null);
        ContourPlot p2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(p1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        p2 = (ContourPlot) in.readObject();
        in.close();
        assertEquals(p1, p2);
    }
