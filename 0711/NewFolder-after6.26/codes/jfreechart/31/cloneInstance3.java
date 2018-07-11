    public void testSerialization() throws IOException, ClassNotFoundException {
        ArrowNeedle n1 = new ArrowNeedle(false);
        ArrowNeedle n2 = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(n1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        n2 = (ArrowNeedle) in.readObject();
        in.close();
        assertTrue(n1.equals(n2));
    }
