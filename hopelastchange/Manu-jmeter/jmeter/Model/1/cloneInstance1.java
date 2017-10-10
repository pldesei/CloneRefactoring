(startLine=67 endLine=72 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/test/src/org/apache/jmeter/functions/TestTimeShiftFunction.java)
    public void testDatePlusOneDay() throws Exception {
        Collection<CompoundVariable> params = makeParams("yyyy-dd-MM", "2017-01-01", "P1D", "");
        function.setParameters(params);
        value = function.execute(result, null);
        assertThat(value, is(equalTo("2017-02-01")));
    }
