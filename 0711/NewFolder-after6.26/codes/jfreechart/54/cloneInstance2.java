    public void testSerialization() throws IOException, ClassNotFoundException {

        XYDataItem i1 = new XYDataItem(1.0, 1.1);
        XYDataItem i2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(i1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        i2 = (XYDataItem) in.readObject();
        in.close();
        assertEquals(i1, i2);

    }
