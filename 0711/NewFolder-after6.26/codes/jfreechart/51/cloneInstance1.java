    public void testSerialization() throws IOException, ClassNotFoundException {
        TimeSeriesCollection c1 = new TimeSeriesCollection(createSeries());
        TimeSeriesCollection c2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(c1);
        out.close();

        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        c2 = (TimeSeriesCollection) in.readObject();
        in.close();
        assertEquals(c1, c2);
    }
