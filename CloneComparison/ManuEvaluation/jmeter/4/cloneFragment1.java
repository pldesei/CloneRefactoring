This clone fragment is located in File: 
test/src/org/apache/jmeter/extractor/TestHtmlExtractorJSoup.java
The line range of this clone fragment is: 97-103
The content of this clone fragment is as follows:
    public void testEmptyDefaultVariable() throws Exception {
        extractor.setExpression("p.missing");
        extractor.setMatchNumber(1);
        extractor.setDefaultEmptyValue(true);
        extractor.process();
        assertEquals("", vars.get("regVal"));
    }
