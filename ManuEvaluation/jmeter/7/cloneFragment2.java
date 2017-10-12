This clone fragment is located in File: 
src/core/org/apache/jmeter/visualizers/RunningSample.java
The line range of this clone fragment is: 358-368
The content of this clone fragment is as follows:
    public String toString() {
        StringBuilder mySB = new StringBuilder();

        mySB.append("Samples: " + this.getNumSamples() + "  ");
        mySB.append("Avg: " + this.getAverage() + "  ");
        mySB.append("Min: " + this.getMin() + "  ");
        mySB.append("Max: " + this.getMax() + "  ");
        mySB.append("Error Rate: " + this.getErrorPercentageString() + "  ");
        mySB.append("Sample Rate: " + this.getRateString());
        return mySB.toString();
    }
