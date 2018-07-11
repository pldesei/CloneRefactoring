            ClassNotFoundException {
        PaintMap m1 = new PaintMap();
        PaintMap m2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(m1);
        out.close();

        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        m2 = (PaintMap) in.readObject();
        in.close();
        assertEquals(m1, m2);
    }
