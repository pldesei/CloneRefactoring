(startLine=97 endLine=103 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/test/src/org/apache/jmeter/extractor/TestHtmlExtractorJSoup.java)
    public void testEmptyDefaultVariable() throws Exception {
        extractor.setExpression("p.missing");
        extractor.setMatchNumber(1);
        extractor.setDefaultEmptyValue(true);
        extractor.process();
        assertEquals("", vars.get("regVal"));
    }
