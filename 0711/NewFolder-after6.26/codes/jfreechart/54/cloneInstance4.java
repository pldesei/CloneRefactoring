    public void testSerialization() throws IOException, ClassNotFoundException {
        YInterval i1 = new YInterval(1.0, 0.5, 1.5);
        YInterval i2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(i1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        i2 = (YInterval) in.readObject();
        in.close();
        assertEquals(i1, i2);
    }
