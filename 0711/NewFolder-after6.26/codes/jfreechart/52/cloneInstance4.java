    public void testSerialization() throws IOException, ClassNotFoundException {
        Month m1 = new Month(12, 1999);
        Month m2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(m1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        m2 = (Month) in.readObject();
        in.close();
        assertEquals(m1, m2);
    }
