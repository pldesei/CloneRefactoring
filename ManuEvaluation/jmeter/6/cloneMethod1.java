This clone method is located in File: 
src/core/org/apache/jmeter/control/gui/LoopControlPanel.java
The line range of this clone method is: 104-111
The content of this clone method is as follows:
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof LoopController) {
            setState(((LoopController) element).getLoopString());
        } else {
            setState(1);
        }
    }
