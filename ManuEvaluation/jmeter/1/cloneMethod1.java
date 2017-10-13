This clone method is located in File: 
test/src/org/apache/jmeter/functions/TestTimeShiftFunction.java
The line range of this clone method is: 67-72
The content of this clone method is as follows:
    public void testDatePlusOneDay() throws Exception {
        Collection<CompoundVariable> params = makeParams("yyyy-dd-MM", "2017-01-01", "P1D", "");
        function.setParameters(params);
        value = function.execute(result, null);
        assertThat(value, is(equalTo("2017-02-01")));
    }
