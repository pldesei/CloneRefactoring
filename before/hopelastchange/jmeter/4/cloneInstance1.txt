This clone instance is located in File: 
test/src/org/apache/jmeter/extractor/TestHtmlExtractorJSoup.java
The line range of this clone instance is: 97-103
The content of this clone instance is as follows:
    public void testEmptyDefaultVariable() throws Exception {
        extractor.setExpression("p.missing");
        extractor.setMatchNumber(1);
        extractor.setDefaultEmptyValue(true);
        extractor.process();
        assertEquals("", vars.get("regVal"));
    }
