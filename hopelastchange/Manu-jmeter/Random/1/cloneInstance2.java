(startLine=90 endLine=97 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/src/core/org/apache/jmeter/control/gui/RunTimeGui.java)
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof RunTime) {
            setState(((RunTime) element).getRuntimeString());
        } else {
            setState(1);
        }
    }
