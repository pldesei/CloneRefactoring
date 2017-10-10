(startLine=76 endLine=91 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/test/src/org/apache/jmeter/extractor/TestJSONPostProcessor.java)
    public void testProcessRandomElementMultipleMatches() {
        JMeterContext context = JMeterContextService.getContext();
        JSONPostProcessor processor = setupProcessor(context, "0", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("$[*]");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar", "[\"one\", \"two\"]");
        processor.process();
        assertThat(vars.get("varname"), CoreMatchers.is(CoreMatchers.anyOf(CoreMatchers.is("one"), CoreMatchers.is("two"))));
        assertThat(vars.get("varname_1"), CoreMatchers.is(CoreMatchers.nullValue()));
        assertThat(vars.get("varname_2"), CoreMatchers.is(CoreMatchers.nullValue()));
        assertThat(vars.get("varname_matchNr"), CoreMatchers.is(CoreMatchers.nullValue()));
    }
