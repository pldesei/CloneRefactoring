    public void testSerialization() throws IOException, ClassNotFoundException {
        ShipNeedle n1 = new ShipNeedle();
        ShipNeedle n2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(n1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        n2 = (ShipNeedle) in.readObject();
        in.close();
        assertTrue(n1.equals(n2));
    }
