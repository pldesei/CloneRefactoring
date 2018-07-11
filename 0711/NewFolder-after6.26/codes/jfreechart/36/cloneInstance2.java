    public void testSerialization() throws IOException, ClassNotFoundException {
        ValueMarker m1 = new ValueMarker(25.0);
        ValueMarker m2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(m1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        m2 = (ValueMarker) in.readObject();
        in.close();
        boolean b = m1.equals(m2);
        assertTrue(b);
    }
