(startLine=104 endLine=111 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/src/core/org/apache/jmeter/control/gui/LoopControlPanel.java)
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof LoopController) {
            setState(((LoopController) element).getLoopString());
        } else {
            setState(1);
        }
    }
