    public void testSerialization() throws IOException, ClassNotFoundException {
        VectorSeries s1 = new VectorSeries("s1");
        s1.add(1.0, 0.5, 1.5, 2.0);
        VectorSeries s2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(s1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        s2 = (VectorSeries) in.readObject();
        in.close();
        assertEquals(s1, s2);
    }
