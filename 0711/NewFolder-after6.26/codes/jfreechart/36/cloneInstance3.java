    public void testSerialization() throws IOException, ClassNotFoundException {
        MeterInterval m1 = new MeterInterval("X", new Range(1.0, 2.0));
        MeterInterval m2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(m1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        m2 = (MeterInterval) in.readObject();
        in.close();
        boolean b = m1.equals(m2);
        assertTrue(b);
    }
