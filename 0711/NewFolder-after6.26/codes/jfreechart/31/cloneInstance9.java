    public void testSerialization() throws IOException, ClassNotFoundException {
        LongNeedle n1 = new LongNeedle();
        LongNeedle n2 = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(n1);
        out.close();
        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        n2 = (LongNeedle) in.readObject();
        in.close();
        assertTrue(n1.equals(n2));
    }
