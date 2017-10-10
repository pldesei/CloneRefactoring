(startLine=75 endLine=80 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/test/src/org/apache/jmeter/functions/TestTimeShiftFunction.java)
    public void testDatePlusOneDayInVariable() throws Exception {
        Collection<CompoundVariable> params = makeParams("yyyy-dd-MM", "2017-01-01", "P1d", "VAR");
        function.setParameters(params);
        function.execute(result, null);
        assertThat(vars.get("VAR"), is(equalTo("2017-02-01")));
    }
