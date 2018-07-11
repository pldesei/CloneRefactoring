    public void testSerialization() throws IOException, ClassNotFoundException {
        PlumNeedle n1 = new PlumNeedle();
        PlumNeedle n2 = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(n1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        n2 = (PlumNeedle) in.readObject();
        in.close();
        assertTrue(n1.equals(n2));
    }
