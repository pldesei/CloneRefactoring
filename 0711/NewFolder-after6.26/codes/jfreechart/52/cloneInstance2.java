    public void testSerialization() throws IOException, ClassNotFoundException {
        FixedMillisecond m1 = new FixedMillisecond();
        FixedMillisecond m2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(m1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        m2 = (FixedMillisecond) in.readObject();
        in.close();
        assertEquals(m1, m2);
    }
