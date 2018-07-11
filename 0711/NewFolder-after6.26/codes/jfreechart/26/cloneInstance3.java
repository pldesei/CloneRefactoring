    public void testSerialization() throws IOException, ClassNotFoundException {
        GridArrangement f1 = new GridArrangement(33, 44);
        GridArrangement f2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(f1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        f2 = (GridArrangement) in.readObject();
        in.close();
        assertEquals(f1, f2);
    }
