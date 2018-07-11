    public void testSerialization3() throws IOException, ClassNotFoundException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(
                "Test Chart", "Category Axis", "Value Axis", dataset,
                PlotOrientation.VERTICAL, true, true, false);
        JFreeChart chart2;

        // serialize and deserialize the chart....
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(chart);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        chart2 = (JFreeChart) in.readObject();
        in.close();

        // now check that the chart is usable...
        try {
            chart2.createBufferedImage(300, 200);
        }
        catch (Exception e) {
            fail("No exception should be thrown.");
        }
    }
