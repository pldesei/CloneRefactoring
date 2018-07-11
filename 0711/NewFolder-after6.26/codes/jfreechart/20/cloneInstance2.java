    public void testSerialization() throws IOException, ClassNotFoundException {
        XYBoxAnnotation a1 = new XYBoxAnnotation(1.0, 2.0, 3.0, 4.0,
                new BasicStroke(1.2f), Color.red, Color.blue);
        XYBoxAnnotation a2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        a2 = (XYBoxAnnotation) in.readObject();
        in.close();
        assertEquals(a1, a2);
    }
