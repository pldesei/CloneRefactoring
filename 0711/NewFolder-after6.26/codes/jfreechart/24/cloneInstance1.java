    public void testSerialization() throws IOException, ClassNotFoundException {
        StandardTickUnitSource t1 = new StandardTickUnitSource();
        StandardTickUnitSource t2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(t1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        t2 = (StandardTickUnitSource) in.readObject();
        in.close();
        assertEquals(t1, t2);
    }
