    public void testSerialization() throws IOException, ClassNotFoundException {
        PointerNeedle n1 = new PointerNeedle();
        PointerNeedle n2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(n1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        n2 = (PointerNeedle) in.readObject();
        in.close();
        assertTrue(n1.equals(n2));
    }
