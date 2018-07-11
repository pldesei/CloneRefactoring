    public void testSerialization() throws IOException, ClassNotFoundException {
        WindNeedle n1 = new WindNeedle();
        WindNeedle n2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(n1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        n2 = (WindNeedle) in.readObject();
        in.close();
        assertTrue(n1.equals(n2));
    }
