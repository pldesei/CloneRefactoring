    public void testSerialization() throws IOException, ClassNotFoundException {
        Millisecond m1 = new Millisecond();
        Millisecond m2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(m1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        m2 = (Millisecond) in.readObject();
        in.close();
        assertEquals(m1, m2);
    }
