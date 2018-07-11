    public void testSerialization() throws IOException, ClassNotFoundException {
        LineFunction2D f1 = new LineFunction2D(1.0, 2.0);
        LineFunction2D f2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(f1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        f2 = (LineFunction2D) in.readObject();
        in.close();
        assertEquals(f1, f2);
    }
