    public void testSerialization() throws IOException, ClassNotFoundException {
        DateTickMarkPosition p1 = DateTickMarkPosition.MIDDLE;
        DateTickMarkPosition p2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(p1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        p2 = (DateTickMarkPosition) in.readObject();
        in.close();
        assertEquals(p1, p2);
        assertTrue(p1 == p2);
    }
