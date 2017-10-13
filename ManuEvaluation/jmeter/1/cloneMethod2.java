This clone method is located in File: 
test/src/org/apache/jmeter/functions/TestTimeShiftFunction.java
The line range of this clone method is: 75-80
The content of this clone method is as follows:
    public void testDatePlusOneDayInVariable() throws Exception {
        Collection<CompoundVariable> params = makeParams("yyyy-dd-MM", "2017-01-01", "P1d", "VAR");
        function.setParameters(params);
        function.execute(result, null);
        assertThat(vars.get("VAR"), is(equalTo("2017-02-01")));
    }
