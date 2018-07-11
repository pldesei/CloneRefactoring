    public void testSerialization() throws IOException, ClassNotFoundException {
        CompassPlot p1 = new CompassPlot(null);
        p1.setRosePaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.blue));
        p1.setRoseCenterPaint(new GradientPaint(4.0f, 3.0f, Color.red, 2.0f,
                1.0f, Color.green));
        p1.setRoseHighlightPaint(new GradientPaint(4.0f, 3.0f, Color.red, 2.0f,
                1.0f, Color.green));
        CompassPlot p2;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(p1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        p2 = (CompassPlot) in.readObject();
        in.close();
        assertEquals(p1, p2);
    }
