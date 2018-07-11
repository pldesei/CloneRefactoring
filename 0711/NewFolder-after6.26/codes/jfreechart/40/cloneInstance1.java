    public void testSerialization() throws IOException, ClassNotFoundException {
        StandardDialFrame f1 = new StandardDialFrame();
        StandardDialFrame f2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(f1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        f2 = (StandardDialFrame) in.readObject();
        in.close();
        assertEquals(f1, f2);
    }
