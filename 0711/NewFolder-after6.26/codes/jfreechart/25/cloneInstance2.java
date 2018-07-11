    public void testSerialization() throws IOException, ClassNotFoundException {
        CategoryAxis a1 = new CategoryAxis("Test Axis");
        a1.setTickLabelPaint("C1", new GradientPaint(1.0f, 2.0f, Color.red,
                3.0f, 4.0f, Color.white));
        CategoryAxis a2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(a1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        a2 = (CategoryAxis) in.readObject();
        in.close();

        assertEquals(a1, a2);
    }
