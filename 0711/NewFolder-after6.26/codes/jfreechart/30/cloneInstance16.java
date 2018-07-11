    public void testSerialization() throws IOException, ClassNotFoundException {
        StandardContourToolTipGenerator g1
            = new StandardContourToolTipGenerator();
        StandardContourToolTipGenerator g2 = null;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(g1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        g2 = (StandardContourToolTipGenerator) in.readObject();
        in.close();
        assertEquals(g1, g2);
    }
