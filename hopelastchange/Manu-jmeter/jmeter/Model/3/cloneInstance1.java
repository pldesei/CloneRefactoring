(startLine=91 endLine=99 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/test/src/org/apache/jmeter/functions/TestTimeShiftFunction.java)
    public void testDefault() throws Exception {
        Collection<CompoundVariable> params = makeParams("", "", "", "");
        function.setParameters(params);
        value = function.execute(result, null);
        long resultat = Long.parseLong(value);
        LocalDateTime  nowFromFunction = LocalDateTime.ofInstant(Instant.ofEpochMilli(resultat), TimeZone
                .getDefault().toZoneId());
        assertThat(nowFromFunction, within(5, ChronoUnit.SECONDS, LocalDateTime.now()));
    }
