    public void testSerialization() throws IOException, ClassNotFoundException {
        XYShapeAnnotation a1 = new XYShapeAnnotation(
                new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0),
                new BasicStroke(1.2f), Color.red, Color.blue);
        XYShapeAnnotation a2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();
        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        a2 = (XYShapeAnnotation) in.readObject();
        in.close();
        assertEquals(a1, a2);
    }
