    public void testSerialization() throws IOException, ClassNotFoundException {
        OHLC i1 = new OHLC(2.0, 4.0, 1.0, 3.0);
        OHLC i2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(i1);
        out.close();

        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        i2 = (OHLC) in.readObject();
        in.close();
        assertEquals(i1, i2);
    }
