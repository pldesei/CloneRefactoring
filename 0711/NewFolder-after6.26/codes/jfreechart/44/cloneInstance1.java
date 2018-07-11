    public void testSerialization() throws IOException, ClassNotFoundException {
        TextTitle t1 = new TextTitle("Test");
        TextTitle t2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(t1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        t2 = (TextTitle) in.readObject();
        in.close();
        assertEquals(t1, t2);
    }
