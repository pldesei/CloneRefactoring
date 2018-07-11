    public void testSerialization() throws IOException, ClassNotFoundException {
        IntervalMarker m1 = new IntervalMarker(45.0, 50.0);
        IntervalMarker m2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(m1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        m2 = (IntervalMarker) in.readObject();
        in.close();
        boolean b = m1.equals(m2);
        assertTrue(b);
    }
