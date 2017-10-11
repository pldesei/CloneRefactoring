This clone fragment is located in File: 
test/src/org/apache/jmeter/extractor/TestHtmlExtractorJSoup.java
The line range of this clone fragment is: 106-112
The content of this clone fragment is as follows:
    public void testNotEmptyDefaultVariable() throws Exception {
        extractor.setExpression("p.missing");
        extractor.setMatchNumber(1);
        extractor.setDefaultEmptyValue(false);
        extractor.process();
        assertNull(vars.get("regVal"));
    }
