    public void testSerialization() throws IOException, ClassNotFoundException {
        TimePeriodValuesCollection c1 = new TimePeriodValuesCollection();
        TimePeriodValuesCollection c2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(c1);
        out.close();

        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        c2 = (TimePeriodValuesCollection) in.readObject();
        in.close();
        assertEquals(c1, c2);
    }
