This clone fragment is located in File: 
src/core/org/apache/jmeter/control/gui/RunTimeGui.java
The line range of this clone fragment is: 90-97
The content of this clone fragment is as follows:
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof RunTime) {
            setState(((RunTime) element).getRuntimeString());
        } else {
            setState(1);
        }
    }
