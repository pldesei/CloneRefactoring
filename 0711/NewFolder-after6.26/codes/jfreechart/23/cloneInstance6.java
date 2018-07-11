    public void testSerialization() throws IOException, ClassNotFoundException {
        DateTickUnit a1 = new DateTickUnit(DateTickUnit.DAY, 7);
        DateTickUnit a2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();

        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        a2 = (DateTickUnit) in.readObject();
        in.close();
        assertEquals(a1, a2);
    }
