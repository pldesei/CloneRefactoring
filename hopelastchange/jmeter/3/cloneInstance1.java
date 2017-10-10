This clone instance is located in File: 
test/src/org/apache/jmeter/functions/TestTimeShiftFunction.java
The line range of this clone instance is: 91-99
The content of this clone instance is as follows:
    public void testDefault() throws Exception {
        Collection<CompoundVariable> params = makeParams("", "", "", "");
        function.setParameters(params);
        value = function.execute(result, null);
        long resultat = Long.parseLong(value);
        LocalDateTime  nowFromFunction = LocalDateTime.ofInstant(Instant.ofEpochMilli(resultat), TimeZone
                .getDefault().toZoneId());
        assertThat(nowFromFunction, within(5, ChronoUnit.SECONDS, LocalDateTime.now()));
    }
