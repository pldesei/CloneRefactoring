    public void testSerialization() throws IOException, ClassNotFoundException {
        CategoryAnchor a1 = CategoryAnchor.MIDDLE;
        CategoryAnchor a2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();
        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        a2 = (CategoryAnchor) in.readObject();
        in.close();
        assertEquals(a1, a2);
        assertTrue(a1 == a2);
    }
