    public void testSerialization() throws IOException, ClassNotFoundException {
        StandardCategoryItemLabelGenerator g1
                = new StandardCategoryItemLabelGenerator("{2}",
                DateFormat.getInstance());
        StandardCategoryItemLabelGenerator g2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(g1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        g2 = (StandardCategoryItemLabelGenerator) in.readObject();
        in.close();
        assertEquals(g1, g2);
    }
