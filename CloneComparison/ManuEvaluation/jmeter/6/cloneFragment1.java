This clone fragment is located in File: 
src/core/org/apache/jmeter/control/gui/LoopControlPanel.java
The line range of this clone fragment is: 104-111
The content of this clone fragment is as follows:
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof LoopController) {
            setState(((LoopController) element).getLoopString());
        } else {
            setState(1);
        }
    }
