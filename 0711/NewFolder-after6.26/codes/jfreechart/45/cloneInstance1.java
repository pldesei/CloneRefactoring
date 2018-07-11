    public void testSerialization() throws IOException, ClassNotFoundException {

        StandardXYURLGenerator g1 = new StandardXYURLGenerator("index.html?");
        StandardXYURLGenerator g2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(g1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        g2 = (StandardXYURLGenerator) in.readObject();
        in.close();
        assertEquals(g1, g2);
    }
