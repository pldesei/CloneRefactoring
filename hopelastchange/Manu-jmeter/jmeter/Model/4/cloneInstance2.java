(startLine=106 endLine=112 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/test/src/org/apache/jmeter/extractor/TestHtmlExtractorJSoup.java)
    public void testNotEmptyDefaultVariable() throws Exception {
        extractor.setExpression("p.missing");
        extractor.setMatchNumber(1);
        extractor.setDefaultEmptyValue(false);
        extractor.process();
        assertNull(vars.get("regVal"));
    }
