    public void testSerialization() throws IOException, ClassNotFoundException {
        ArcDialFrame f1 = new ArcDialFrame();
        ArcDialFrame f2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(f1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        f2 = (ArcDialFrame) in.readObject();
        in.close();
        assertEquals(f1, f2);
    }
