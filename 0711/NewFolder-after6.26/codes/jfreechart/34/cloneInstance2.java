    public void testSerialization() throws IOException, ClassNotFoundException {
        ThermometerPlot p1 = new ThermometerPlot();
        ThermometerPlot p2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(p1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        p2 = (ThermometerPlot) in.readObject();
        in.close();
        assertTrue(p1.equals(p2));
    }
