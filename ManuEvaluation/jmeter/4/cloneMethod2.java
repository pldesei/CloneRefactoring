This clone method is located in File: 
test/src/org/apache/jmeter/extractor/TestHtmlExtractorJSoup.java
The line range of this clone method is: 106-112
The content of this clone method is as follows:
    public void testNotEmptyDefaultVariable() throws Exception {
        extractor.setExpression("p.missing");
        extractor.setMatchNumber(1);
        extractor.setDefaultEmptyValue(false);
        extractor.process();
        assertNull(vars.get("regVal"));
    }
