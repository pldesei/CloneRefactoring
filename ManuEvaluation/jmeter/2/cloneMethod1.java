This clone method is located in File: 
test/src/org/apache/jmeter/reporters/TestResultSaver.java
The line range of this clone method is: 69-82
The content of this clone method is as follows:
    public void testSuccessWithVariable() {
        sampleResult.setSuccessful(true);
        resultSaver.setProperty(ResultSaver.NUMBER_PAD_LENGTH, "5");
        resultSaver.setProperty(ResultSaver.VARIABLE_NAME,"myVar");
        resultSaver.testStarted();
        resultSaver.sampleOccurred(new SampleEvent(sampleResult, "JUnit-TG"));
        String fileName = sampleResult.getResultFileName();
        Assert.assertNotNull(fileName);
        Assert.assertEquals("00001.unknown", fileName);
        File file = new File(FileServer.getDefaultBase(), fileName);
        Assert.assertTrue(file.exists());
        Assert.assertTrue(file.delete());
        Assert.assertEquals("00001.unknown", vars.get("myVar"));
    }
