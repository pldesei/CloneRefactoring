    public void testSerialization() throws IOException, ClassNotFoundException {
        DefaultIntervalXYDataset d1 = new DefaultIntervalXYDataset();
        DefaultIntervalXYDataset d2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(d1);
        out.close();

        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        d2 = (DefaultIntervalXYDataset) in.readObject();
        in.close();
        assertEquals(d1, d2);

        // try a dataset with some content...
        d1 = createSampleDataset1();
        buffer = new ByteArrayOutputStream();
        out = new ObjectOutputStream(buffer);
        out.writeObject(d1);
        out.close();

        in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        d2 = (DefaultIntervalXYDataset) in.readObject();
        in.close();
        assertEquals(d1, d2);
    }
