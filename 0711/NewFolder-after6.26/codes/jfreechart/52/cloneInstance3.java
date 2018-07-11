    public void testSerialization() throws IOException, ClassNotFoundException {
        Minute m1 = new Minute();
        Minute m2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(m1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        m2 = (Minute) in.readObject();
        in.close();
        assertEquals(m1, m2);
    }
