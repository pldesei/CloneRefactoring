    public void testSerialization() throws IOException, ClassNotFoundException {
        LegendItemEntity e1 = new LegendItemEntity(new Rectangle2D.Double(1.0,
                2.0, 3.0, 4.0));
        LegendItemEntity e2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(e1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        e2 = (LegendItemEntity) in.readObject();
        in.close();
        assertEquals(e1, e2);
    }
