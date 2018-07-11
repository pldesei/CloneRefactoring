    public void testSerialization() throws IOException, ClassNotFoundException {
        PinNeedle n1 = new PinNeedle();
        PinNeedle n2 = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(n1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        n2 = (PinNeedle) in.readObject();
        in.close();
        assertTrue(n1.equals(n2));
    }
