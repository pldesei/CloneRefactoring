(startLine=122 endLine=131 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/test/src/org/apache/jmeter/functions/TestTimeShiftFunction.java)
    public void testWrongAmountToAdd() throws Exception {
        // Nothing is add with wrong value, so check if return is now
        Collection<CompoundVariable> params = makeParams("", "", "qefv1Psd", "");
        function.setParameters(params);
        value = function.execute(result, null);
        long resultat = Long.parseLong(value);
        LocalDateTime  nowFromFunction = LocalDateTime.ofInstant(Instant.ofEpochMilli(resultat), TimeZone
                .getDefault().toZoneId());
        assertThat(nowFromFunction, within(5, ChronoUnit.SECONDS, LocalDateTime.now()));
    }
