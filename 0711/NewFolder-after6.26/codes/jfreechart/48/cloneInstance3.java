    public void testSerialization() throws IOException, ClassNotFoundException {
        PolynomialFunction2D f1 = new PolynomialFunction2D(new double[] {1.0,
                2.0});
        PolynomialFunction2D f2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(f1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
               buffer.toByteArray()));
        f2 = (PolynomialFunction2D) in.readObject();
        in.close();
        assertEquals(f1, f2);
    }
