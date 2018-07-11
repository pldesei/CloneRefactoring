    public void testSerialization() throws IOException, ClassNotFoundException {
        ContourEntity e1 = new ContourEntity(new Rectangle2D.Double(1.0, 2.0, 
                3.0, 4.0), "ToolTip", "URL");
        ContourEntity e2 = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(e1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        e2 = (ContourEntity) in.readObject();
        in.close();
        assertEquals(e1, e2);
    }
