This clone method is located in File: 
test/src/org/apache/jmeter/functions/TestTimeShiftFunction.java
The line range of this clone method is: 122-131
The content of this clone method is as follows:
    public void testWrongAmountToAdd() throws Exception {
        Collection<CompoundVariable> params = makeParams("", "", "qefv1Psd", "");
        function.setParameters(params);
        value = function.execute(result, null);
        long resultat = Long.parseLong(value);
        LocalDateTime  nowFromFunction = LocalDateTime.ofInstant(Instant.ofEpochMilli(resultat), TimeZone
                .getDefault().toZoneId());
        assertThat(nowFromFunction, within(5, ChronoUnit.SECONDS, LocalDateTime.now()));
    }
