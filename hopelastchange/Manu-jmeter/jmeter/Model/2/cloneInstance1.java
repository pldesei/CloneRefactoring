(startLine=69 endLine=82 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/test/src/org/apache/jmeter/reporters/TestResultSaver.java)
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
