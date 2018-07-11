    public void testSerialization() throws IOException, ClassNotFoundException {

        XYIntervalSeries s1 = new XYIntervalSeries("s1");
        s1.add(1.0, 0.5, 1.5, 2.0, 1.9, 2.1);
        XYIntervalSeries s2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(s1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        s2 = (XYIntervalSeries) in.readObject();
        in.close();
        assertEquals(s1, s2);
    }
