    public void testSerialization() throws IOException, ClassNotFoundException {
        CombinedDomainXYPlot plot1 = createPlot();
        CombinedDomainXYPlot plot2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(plot1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        plot2 = (CombinedDomainXYPlot) in.readObject();
        in.close();
        assertEquals(plot1, plot2);
    }
