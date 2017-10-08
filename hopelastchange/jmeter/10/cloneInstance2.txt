This clone instance is located in File: 
test/src/org/apache/jmeter/control/TestWhileController.java
The line range of this clone instance is: 225-273
The content of this clone instance is as follows:
        public void testVariable2() throws Exception {
            GenericController controller = new GenericController();
            WhileController while_cont = new WhileController();
            setLastSampleStatus(false);
            while_cont.setCondition("${VAR}");
            jmvars.put("VAR", "");
            ValueReplacer vr = new ValueReplacer();
            vr.replaceValues(while_cont);
            setRunning(while_cont);
            controller.addTestElement(new TestSampler("before"));
            controller.addTestElement(while_cont);
            GenericController simple = new GenericController();
            while_cont.addTestElement(simple);
            simple.addTestElement(new TestSampler("one"));
            simple.addTestElement(new TestSampler("two"));
            while_cont.addTestElement(new TestSampler("three"));
            while_cont.addTestElement(new TestSampler("four"));
            controller.addTestElement(new TestSampler("after"));
            controller.initialize();
            for (int i = 1; i <= 3; i++) {
                assertEquals("Loop: "+i,"before", nextName(controller));
                assertEquals("Loop: "+i,"one", nextName(controller));
                assertEquals("Loop: "+i,"two", nextName(controller));
                assertEquals("Loop: "+i,"three", nextName(controller));
                assertEquals("Loop: "+i,"four", nextName(controller));
                assertEquals("Loop: "+i,"after", nextName(controller));
                assertNull("Loop: "+i,nextName(controller));
            }
            jmvars.put("VAR", "LAST"); // Should not enter the loop
            for (int i = 1; i <= 3; i++) {
                assertEquals("Loop: "+i,"before", nextName(controller));
                assertEquals("Loop: "+i,"after", nextName(controller));
                assertNull("Loop: "+i,nextName(controller));
            }
            jmvars.put("VAR", "");
            for (int i = 1; i <= 3; i++) {
                assertEquals("Loop: "+i,"before", nextName(controller));
                if (i==1){
                    assertEquals("Loop: "+i,"one", nextName(controller));
                    assertEquals("Loop: "+i,"two", nextName(controller));
                    jmvars.put("VAR", "LAST"); // Should not enter the loop next time
                    // But should continue to the end of the loop
                    assertEquals("Loop: "+i,"three", nextName(controller));
                    assertEquals("Loop: "+i,"four", nextName(controller));
                }
                assertEquals("Loop: "+i,"after", nextName(controller));
                assertNull("Loop: "+i,nextName(controller));
            }
        }
